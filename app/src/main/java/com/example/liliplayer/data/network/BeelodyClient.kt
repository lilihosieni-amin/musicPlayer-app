package com.example.liliplayer.data.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.File
import java.net.URI
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

data class DownloadLink(
    val url: String,
    val filename: String
)

@Singleton
class BeelodyClient @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Simple in-memory cookie jar that works reliably on all Android versions
    private val simpleCookieJar = object : CookieJar {
        private val storage = mutableListOf<Cookie>()

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            // Remove old cookies with same name+domain, then add new ones
            cookies.forEach { newCookie ->
                storage.removeAll { it.name == newCookie.name && it.domain == newCookie.domain }
            }
            storage.addAll(cookies)
        }

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            // Remove expired cookies
            storage.removeAll { it.expiresAt < System.currentTimeMillis() }
            // Return cookies that match this URL
            return storage.filter { it.matches(url) }
        }

        fun hasLoggedInCookie(): Boolean {
            return storage.any { it.name.startsWith("wordpress_logged_in") }
        }
    }

    private val client = OkHttpClient.Builder()
        .cookieJar(simpleCookieJar)
        .followRedirects(true)
        .followSslRedirects(true)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 8.0)")
                    .header("Referer", "https://beelody.net")
                    .build()
            )
        }
        .build()

    private var isLoggedIn = false

    suspend fun ensureLoggedIn(email: String, password: String) = withContext(Dispatchers.IO) {
        if (!isLoggedIn || !simpleCookieJar.hasLoggedInCookie()) {
            val result = login(email, password)
            if (result.isFailure) {
                throw result.exceptionOrNull() ?: Exception("Login failed")
            }
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Primary approach: POST directly to wp-login.php with known fields
            val result = directLogin(email, password)
            if (result.isSuccess) return@withContext result

            // Fallback: parse the login page form
            val loginPageRequest = Request.Builder()
                .url("https://beelody.net/login")
                .get()
                .build()

            val loginPageResponse = client.newCall(loginPageRequest).execute()
            val loginPageHtml = loginPageResponse.body?.string() ?: return@withContext Result.failure(
                Exception("Failed to load login page")
            )

            val doc = Jsoup.parse(loginPageHtml)
            val form = doc.selectFirst("form#loginform")
                ?: doc.selectFirst("form[action*=wp-login]")
                ?: doc.selectFirst("form[name=loginform]")
                ?: return@withContext Result.failure(Exception("Login form not found"))

            val actionUrl = form.attr("action").ifEmpty { "https://beelody.net/wp-login.php" }

            val formBuilder = FormBody.Builder()
            form.select("input[type=hidden]").forEach { input ->
                val name = input.attr("name")
                val value = input.attr("value")
                if (name.isNotEmpty()) {
                    formBuilder.add(name, value)
                }
            }

            formBuilder.add(
                form.selectFirst("input[name=log]")?.attr("name") ?: "log",
                email
            )
            formBuilder.add(
                form.selectFirst("input[name=pwd]")?.attr("name") ?: "pwd",
                password
            )

            val loginRequest = Request.Builder()
                .url(actionUrl)
                .post(formBuilder.build())
                .build()

            client.newCall(loginRequest).execute()

            if (simpleCookieJar.hasLoggedInCookie()) {
                isLoggedIn = true
                return@withContext Result.success(Unit)
            }

            Result.failure(Exception("Login failed: no session cookie set"))
        } catch (e: Exception) {
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }

    private fun directLogin(email: String, password: String): Result<Unit> {
        // Step 1: GET login page to receive the test cookie (WordPress requires this)
        val getRequest = Request.Builder()
            .url("https://beelody.net/wp-login.php")
            .get()
            .build()
        client.newCall(getRequest).execute()

        // Step 2: POST credentials with proper WordPress form fields
        val formBody = FormBody.Builder()
            .add("log", email)
            .add("pwd", password)
            .add("wp-submit", "Log In")
            .add("rememberme", "forever")
            .add("redirect_to", "https://beelody.net")
            .add("testcookie", "1")
            .build()

        val request = Request.Builder()
            .url("https://beelody.net/wp-login.php")
            .post(formBody)
            .build()

        client.newCall(request).execute()

        return if (simpleCookieJar.hasLoggedInCookie()) {
            isLoggedIn = true
            Result.success(Unit)
        } else {
            Result.failure(Exception("Login failed: invalid credentials"))
        }
    }

    suspend fun resolveDownloadLinks(url: String): Result<List<DownloadLink>> = withContext(Dispatchers.IO) {
        try {
            val safeUrl = sanitizeUrl(url)

            if (safeUrl.contains("vip-dl")) {
                val filename = extractFilenameFromUrl(safeUrl)
                return@withContext Result.success(listOf(DownloadLink(safeUrl, filename)))
            }

            // Song/album page — parse HTML for download links
            val request = Request.Builder().url(buildPreservedUrl(safeUrl)).get().build()
            val response = client.newCall(request).execute()
            val html = response.body?.string() ?: return@withContext Result.failure(
                Exception("Failed to load page")
            )

            val doc = Jsoup.parse(html)
            val links = mutableListOf<DownloadLink>()

            // Priority 1: Individual track links from data-dl2 (FLAC) or data-dl1 (MP3)
            // Beelody stores per-track downloads in <li data-dl1="mp3-url" data-dl2="flac-url">
            val trackElements = doc.select("[data-dl1]")
            for (element in trackElements) {
                // Prefer FLAC (data-dl2), fall back to MP3 (data-dl1)
                val dlUrl = element.attr("data-dl2").ifEmpty { element.attr("data-dl1") }
                if (dlUrl.isNotEmpty() && dlUrl.contains("vip-dl")) {
                    val filename = extractFilenameFromUrl(dlUrl)
                    links.add(DownloadLink(sanitizeUrl(dlUrl), filename))
                }
            }

            // Priority 2: ZIP album links from <a href*=vip-dl> (if no individual tracks found)
            if (links.isEmpty()) {
                doc.select("a[href*=vip-dl]").forEach { element ->
                    val href = element.attr("href")
                    if (href.isNotEmpty()) {
                        val filename = extractFilenameFromUrl(href)
                        links.add(DownloadLink(sanitizeUrl(href), filename))
                    }
                }
            }

            if (links.isEmpty()) {
                return@withContext Result.failure(Exception("No download links found on this page"))
            }

            Result.success(links)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to resolve links: ${e.message}"))
        }
    }

    suspend fun resolveCdnUrl(vipDlUrl: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val safeUrl = sanitizeUrl(vipDlUrl)
            val httpUrl = buildPreservedUrl(safeUrl)

            val request = Request.Builder().url(httpUrl).get().build()
            val response = client.newCall(request).execute()

            // Check if we were redirected to the login page (OkHttp follows redirects)
            val finalUrl = response.request.url.toString()
            if (finalUrl.contains("wp-login")) {
                return@withContext Result.failure(SessionExpiredException("Not logged in (redirected to login)"))
            }

            val html = response.body?.string() ?: return@withContext Result.failure(
                Exception("Empty response (0 bytes) from: ${httpUrl}")
            )

            if (html.isBlank()) {
                return@withContext Result.failure(
                    Exception("Blank page (${html.length} chars). URL: ${httpUrl}")
                )
            }

            // Try multiple regex patterns for the CDN redirect
            val patterns = listOf(
                Regex("""window\.location\s*=\s*"([^"]+)""""),
                Regex("""window\.location\.href\s*=\s*"([^"]+)""""),
                Regex("""location\.replace\s*\(\s*"([^"]+)""""),
                Regex("""<meta\s+http-equiv\s*=\s*"refresh"\s+content\s*=\s*"\d+;\s*url=([^"]+)"""", RegexOption.IGNORE_CASE)
            )

            for (pattern in patterns) {
                val match = pattern.find(html)
                if (match != null) {
                    return@withContext Result.success(match.groupValues[1])
                }
            }

            // Check if page has login form (session lost)
            if (html.contains("id=\"loginform\"")) {
                return@withContext Result.failure(SessionExpiredException("Not logged in"))
            }

            // Page loaded but no download redirect — link is likely incomplete or invalid
            if (html.length > 10000 && !html.contains("شروع دانلود")) {
                return@withContext Result.failure(Exception(
                    "Download link incomplete or invalid. Try pasting the song page URL instead of the vip-dl link."
                ))
            }

            Result.failure(Exception("Could not find download on this page"))
        } catch (e: Exception) {
            Result.failure(Exception("Failed to resolve CDN URL: ${e.message}"))
        }
    }

    suspend fun downloadFile(
        vipDlUrl: String,
        destDir: File,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        val cdnUrlResult = resolveCdnUrl(vipDlUrl)
        val cdnUrl = cdnUrlResult.getOrElse { return@withContext Result.failure(it) }

        downloadFromCdn(cdnUrl, destDir, extractFilenameFromUrl(vipDlUrl), onProgress)
    }

    private fun downloadFromCdn(
        cdnUrl: String,
        destDir: File,
        fallbackFilename: String,
        onProgress: (Int) -> Unit
    ): Result<File> {
        val request = Request.Builder().url(cdnUrl).get().build()
        val response = client.newCall(request).execute()
        val body = response.body ?: return Result.failure(Exception("Empty response from CDN"))

        val contentType = response.header("Content-Type", "") ?: ""
        if (contentType.contains("text/html")) {
            return Result.failure(SessionExpiredException("Session expired: CDN returned HTML"))
        }

        // Determine filename from Content-Disposition or fallback
        val disposition = response.header("Content-Disposition")
        val filename = if (disposition != null) {
            val match = Regex("""filename\*?=(?:UTF-8''|"?)([^";]+)""").find(disposition)
            match?.groupValues?.get(1)?.let { java.net.URLDecoder.decode(it.trim('"'), "UTF-8") }
                ?: fallbackFilename
        } else {
            fallbackFilename
        }

        destDir.mkdirs()
        val outFile = File(destDir, filename)
        val contentLength = body.contentLength()
        var bytesWritten = 0L

        outFile.outputStream().use { out ->
            body.byteStream().use { input ->
                val buffer = ByteArray(8192)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    out.write(buffer, 0, read)
                    bytesWritten += read
                    if (contentLength > 0) {
                        onProgress(((bytesWritten * 100) / contentLength).toInt())
                    }
                }
            }
        }

        onProgress(100)
        return Result.success(outFile)
    }

    /**
     * Build an OkHttp HttpUrl preserving exact query encoding.
     * OkHttp's default URL parsing re-encodes characters like () and / in query strings,
     * which breaks beelody.net's server that expects them as-is.
     */
    private fun buildPreservedUrl(urlString: String): HttpUrl {
        val uri = URI(urlString)
        val builder = HttpUrl.Builder()
            .scheme(uri.scheme ?: "https")
            .host(uri.host ?: "")
        if (uri.port != -1) builder.port(uri.port)
        builder.encodedPath(uri.rawPath ?: "/")
        uri.rawQuery?.let { builder.encodedQuery(it) }
        return builder.build()
    }

    private fun sanitizeUrl(url: String): String {
        return url.trim()
            .replace("[", "%5B")
            .replace("]", "%5D")
            .replace(" ", "%20")
            .replace("#", "%23")
    }

    private fun extractFilenameFromUrl(url: String): String {
        return try {
            val decoded = java.net.URLDecoder.decode(url, "UTF-8")
            if (decoded.contains("filename=")) {
                val filenameParam = decoded.substringAfter("filename=").substringBefore("&")
                filenameParam.substringAfterLast("/").ifEmpty { "download" }
            } else {
                decoded.substringAfterLast("/").substringBefore("&").substringBefore("?")
                    .ifEmpty { "download" }
            }
        } catch (e: Exception) {
            "download"
        }
    }

    class SessionExpiredException(message: String) : Exception(message)
}

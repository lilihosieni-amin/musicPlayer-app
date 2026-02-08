package com.example.liliplayer.data.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsApiService @Inject constructor() {

    suspend fun fetchLyrics(artist: String, title: String): String? = withContext(Dispatchers.IO) {
        try {
            val encodedArtist = URLEncoder.encode(artist, "UTF-8")
            val encodedTitle = URLEncoder.encode(title, "UTF-8")
            val url = URL("https://lrclib.net/api/get?artist_name=$encodedArtist&track_name=$encodedTitle")

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "LiliPlayer/1.0")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)

                // Prefer synced lyrics, fall back to plain
                val syncedLyrics = json.optString("syncedLyrics", "")
                val plainLyrics = json.optString("plainLyrics", "")

                when {
                    syncedLyrics.isNotBlank() -> syncedLyrics
                    plainLyrics.isNotBlank() -> plainLyrics
                    else -> null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

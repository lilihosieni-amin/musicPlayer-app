package com.example.liliplayer.data.repository

import com.example.liliplayer.data.network.BeelodyClient
import com.example.liliplayer.data.network.BeelodyFileManager
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeelodyRepository @Inject constructor(
    private val beelodyClient: BeelodyClient,
    private val fileManager: BeelodyFileManager
) {
    sealed class DownloadState {
        data object Idle : DownloadState()
        data object LoggingIn : DownloadState()
        data object ParsingPage : DownloadState()
        data class Downloading(
            val currentFile: String,
            val currentIndex: Int,
            val totalFiles: Int,
            val progress: Int
        ) : DownloadState()
        data object Extracting : DownloadState()
        data object Scanning : DownloadState()
        data class Success(val fileCount: Int) : DownloadState()
        data class Error(val message: String) : DownloadState()
    }

    companion object {
        const val DEFAULT_EMAIL = "Lilihosieni2003@gmail.com"
        const val DEFAULT_PASSWORD = "lili_2003"
    }

    suspend fun download(
        url: String,
        onStateChange: (DownloadState) -> Unit
    ) {
        try {
            // Step 1: Login
            onStateChange(DownloadState.LoggingIn)
            beelodyClient.ensureLoggedIn(DEFAULT_EMAIL, DEFAULT_PASSWORD)

            // Step 2: Resolve download links
            onStateChange(DownloadState.ParsingPage)
            val linksResult = beelodyClient.resolveDownloadLinks(url)
            val links = linksResult.getOrElse {
                onStateChange(DownloadState.Error(it.message ?: "Failed to resolve links"))
                return
            }

            val destDir = fileManager.getDownloadDir()
            val allDownloadedFiles = mutableListOf<File>()

            // Step 3: Download each link
            for ((index, link) in links.withIndex()) {
                val displayName = fileManager.cleanFilename(link.filename).cleanFilename
                onStateChange(
                    DownloadState.Downloading(
                        currentFile = displayName,
                        currentIndex = index + 1,
                        totalFiles = links.size,
                        progress = 0
                    )
                )

                val fileResult = downloadWithRetry(link.url, destDir) { progress ->
                    onStateChange(
                        DownloadState.Downloading(
                            currentFile = displayName,
                            currentIndex = index + 1,
                            totalFiles = links.size,
                            progress = progress
                        )
                    )
                }

                val file = fileResult.getOrElse {
                    onStateChange(DownloadState.Error(it.message ?: "Download failed"))
                    return
                }

                // Step 4: Handle ZIP or single file
                if (fileManager.isZipFile(file)) {
                    onStateChange(DownloadState.Extracting)
                    val extracted = fileManager.extractZip(file, destDir)
                    allDownloadedFiles.addAll(extracted)
                } else {
                    // Rename to clean filename
                    val info = fileManager.cleanFilename(file.name)
                    val cleanFile = File(destDir, info.cleanFilename)
                    if (cleanFile.absolutePath != file.absolutePath) {
                        file.renameTo(cleanFile)
                        allDownloadedFiles.add(cleanFile)
                    } else {
                        allDownloadedFiles.add(file)
                    }
                }
            }

            // Step 5: Scan files
            onStateChange(DownloadState.Scanning)
            fileManager.scanFiles(allDownloadedFiles)

            // Done
            onStateChange(DownloadState.Success(allDownloadedFiles.size))
        } catch (e: Exception) {
            onStateChange(DownloadState.Error(e.message ?: "Unknown error"))
        }
    }

    private suspend fun downloadWithRetry(
        vipDlUrl: String,
        destDir: File,
        onProgress: (Int) -> Unit
    ): Result<File> {
        val result = beelodyClient.downloadFile(vipDlUrl, destDir, onProgress)

        // If session expired, re-login and retry once
        if (result.isFailure && result.exceptionOrNull() is BeelodyClient.SessionExpiredException) {
            val loginResult = beelodyClient.login(DEFAULT_EMAIL, DEFAULT_PASSWORD)
            if (loginResult.isFailure) {
                return Result.failure(loginResult.exceptionOrNull() ?: Exception("Re-login failed"))
            }
            return beelodyClient.downloadFile(vipDlUrl, destDir, onProgress)
        }

        return result
    }
}

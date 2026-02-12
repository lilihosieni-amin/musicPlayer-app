package com.example.liliplayer.data.network

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

data class TrackInfo(
    val artist: String,
    val title: String,
    val year: String,
    val extension: String,
    val cleanFilename: String
)

@Singleton
class BeelodyFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val audioExtensions = listOf(".mp3", ".flac", ".m4a", ".wav", ".ogg", ".aac")

    fun getDownloadDir(): File {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Beelody"
        )
        dir.mkdirs()
        return dir
    }

    fun cleanFilename(raw: String): TrackInfo {
        var name = raw
        val ext = audioExtensions.firstOrNull { name.lowercase().endsWith(it) }
            ?: if (name.lowercase().endsWith(".zip")) ".zip" else ""
        name = name.removeSuffix(ext)

        // Remove [Beelody]
        name = name.replace(Regex("""\s*\[Beelody]""", RegexOption.IGNORE_CASE), "")

        // Extract year
        val year = Regex("""\((\d{4})\)""").find(name)?.groupValues?.get(1) ?: ""

        // Remove year
        name = name.replace(Regex("""\s*\(\d{4}\)"""), "")

        // Remove leading track numbers like "05 " or "05. " or "05 - "
        name = name.replace(Regex("""^\d{1,3}[\s.\-]+"""), "")

        name = name.trim().trimEnd('-').trim()

        val (artist, title) = if (" - " in name) {
            val parts = name.split(" - ", limit = 2)
            parts[0].trim() to parts[1].trim()
        } else {
            "" to name
        }

        val cleanName = if (artist.isNotEmpty()) "$artist - $title$ext" else "$title$ext"
        return TrackInfo(artist, title, year, ext, cleanName)
    }

    fun extractZip(zipFile: File, destDir: File): List<File> {
        val extracted = mutableListOf<File>()

        ZipInputStream(FileInputStream(zipFile)).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory) {
                    val fileName = entry.name.substringAfterLast("/")
                    if (audioExtensions.any { fileName.lowercase().endsWith(it) }) {
                        val info = cleanFilename(fileName)
                        val outFile = File(destDir, info.cleanFilename)
                        outFile.outputStream().use { out ->
                            zis.copyTo(out)
                        }
                        extracted.add(outFile)
                    }
                }
                entry = zis.nextEntry
            }
        }

        zipFile.delete()
        return extracted
    }

    fun scanFiles(files: List<File>) {
        if (files.isEmpty()) return
        MediaScannerConnection.scanFile(
            context,
            files.map { it.absolutePath }.toTypedArray(),
            null,
            null
        )
    }

    fun isZipFile(file: File): Boolean {
        return file.name.lowercase().endsWith(".zip")
    }
}

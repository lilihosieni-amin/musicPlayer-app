package com.example.liliplayer.data.repository

import android.net.Uri
import com.example.liliplayer.data.network.LyricsApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsRepository @Inject constructor(
    private val lyricsApiService: LyricsApiService
) {
    private val cache = mutableMapOf<String, String?>()

    suspend fun getLyrics(artist: String, title: String, contentUri: Uri?): String? {
        val key = "$artist|$title"
        cache[key]?.let { return it }

        // Try online (LRCLIB)
        val online = lyricsApiService.fetchLyrics(artist, title)
        if (online != null) {
            cache[key] = online
            return online
        }

        return null
    }
}

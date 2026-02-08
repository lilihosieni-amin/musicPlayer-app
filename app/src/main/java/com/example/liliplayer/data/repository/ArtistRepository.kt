package com.example.liliplayer.data.repository

import com.example.liliplayer.data.mediastore.MediaStoreScanner
import com.example.liliplayer.domain.model.Artist
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArtistRepository @Inject constructor(
    private val mediaStoreScanner: MediaStoreScanner
) {
    private var cachedArtists: List<Artist> = emptyList()

    suspend fun getAllArtists(): List<Artist> {
        if (cachedArtists.isEmpty()) {
            cachedArtists = mediaStoreScanner.queryArtists()
        }
        return cachedArtists
    }

    suspend fun getArtistById(artistId: Long): Artist? {
        return getAllArtists().find { it.id == artistId }
    }

    suspend fun searchArtists(query: String): List<Artist> {
        val lowerQuery = query.lowercase()
        return getAllArtists().filter {
            it.name.lowercase().contains(lowerQuery)
        }
    }

    fun invalidateCache() {
        cachedArtists = emptyList()
    }
}

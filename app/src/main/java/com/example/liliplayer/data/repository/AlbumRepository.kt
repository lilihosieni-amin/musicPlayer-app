package com.example.liliplayer.data.repository

import com.example.liliplayer.data.mediastore.MediaStoreScanner
import com.example.liliplayer.domain.model.Album
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    private val mediaStoreScanner: MediaStoreScanner
) {
    private var cachedAlbums: List<Album> = emptyList()

    suspend fun getAllAlbums(): List<Album> {
        if (cachedAlbums.isEmpty()) {
            cachedAlbums = mediaStoreScanner.queryAlbums()
        }
        return cachedAlbums
    }

    suspend fun getAlbumById(albumId: Long): Album? {
        return getAllAlbums().find { it.id == albumId }
    }

    suspend fun getAlbumsForArtist(artistId: Long): List<Album> {
        return getAllAlbums().filter { it.artistId == artistId }
    }

    suspend fun searchAlbums(query: String): List<Album> {
        val lowerQuery = query.lowercase()
        return getAllAlbums().filter {
            it.name.lowercase().contains(lowerQuery) ||
            it.artist.lowercase().contains(lowerQuery)
        }
    }

    fun invalidateCache() {
        cachedAlbums = emptyList()
    }
}

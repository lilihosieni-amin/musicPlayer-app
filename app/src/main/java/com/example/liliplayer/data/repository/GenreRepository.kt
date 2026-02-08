package com.example.liliplayer.data.repository

import com.example.liliplayer.data.mediastore.MediaStoreScanner
import com.example.liliplayer.domain.model.Genre
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenreRepository @Inject constructor(
    private val mediaStoreScanner: MediaStoreScanner,
    private val songRepository: SongRepository
) {
    private var cachedGenres: List<Genre> = emptyList()

    suspend fun getAllGenres(): List<Genre> {
        if (cachedGenres.isEmpty()) {
            cachedGenres = mediaStoreScanner.queryGenres()
        }
        return cachedGenres
    }

    suspend fun getGenreById(genreId: Long): Genre? {
        return getAllGenres().find { it.id == genreId }
    }

    suspend fun getSongsForGenre(genreId: Long): List<com.example.liliplayer.domain.model.Song> {
        val songIds = mediaStoreScanner.querySongsForGenre(genreId)
        return songRepository.getSongsByIds(songIds)
    }

    suspend fun searchGenres(query: String): List<Genre> {
        val lowerQuery = query.lowercase()
        return getAllGenres().filter {
            it.name.lowercase().contains(lowerQuery)
        }
    }

    fun invalidateCache() {
        cachedGenres = emptyList()
    }
}

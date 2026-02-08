package com.example.liliplayer.data.repository

import com.example.liliplayer.data.mediastore.DeleteResult
import com.example.liliplayer.data.mediastore.MediaStoreScanner
import com.example.liliplayer.data.mediastore.UpdateResult
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.domain.model.SortOrder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SongRepository @Inject constructor(
    private val mediaStoreScanner: MediaStoreScanner
) {
    private var cachedSongs: List<Song> = emptyList()

    suspend fun getAllSongs(): List<Song> {
        if (cachedSongs.isEmpty()) {
            cachedSongs = mediaStoreScanner.querySongs()
        }
        return cachedSongs
    }

    suspend fun getSongsSorted(sortOrder: SortOrder): List<Song> {
        val songs = getAllSongs()
        return when (sortOrder) {
            SortOrder.TITLE_ASC -> songs.sortedBy { it.title.lowercase() }
            SortOrder.TITLE_DESC -> songs.sortedByDescending { it.title.lowercase() }
            SortOrder.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
            SortOrder.ARTIST_DESC -> songs.sortedByDescending { it.artist.lowercase() }
            SortOrder.ALBUM_ASC -> songs.sortedBy { it.album.lowercase() }
            SortOrder.ALBUM_DESC -> songs.sortedByDescending { it.album.lowercase() }
            SortOrder.YEAR_ASC -> songs.sortedBy { it.year }
            SortOrder.YEAR_DESC -> songs.sortedByDescending { it.year }
            SortOrder.DURATION_ASC -> songs.sortedBy { it.duration }
            SortOrder.DURATION_DESC -> songs.sortedByDescending { it.duration }
            SortOrder.DATE_ADDED_ASC -> songs.sortedBy { it.dateAdded }
            SortOrder.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
        }
    }

    suspend fun getSongsForAlbum(albumId: Long): List<Song> {
        return getAllSongs().filter { it.albumId == albumId }.sortedBy { it.trackNumber }
    }

    suspend fun getSongsForArtist(artistId: Long): List<Song> {
        return getAllSongs().filter { it.artistId == artistId }
    }

    suspend fun getSongById(songId: Long): Song? {
        return getAllSongs().find { it.id == songId }
    }

    suspend fun getSongsByIds(ids: List<Long>): List<Song> {
        val songMap = getAllSongs().associateBy { it.id }
        return ids.mapNotNull { songMap[it] }
    }

    suspend fun searchSongs(query: String): List<Song> {
        val lowerQuery = query.lowercase()
        return getAllSongs().filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.artist.lowercase().contains(lowerQuery) ||
            it.album.lowercase().contains(lowerQuery)
        }
    }

    suspend fun updateSongMetadata(songId: Long, title: String, artist: String, album: String): UpdateResult {
        val result = mediaStoreScanner.updateSongMetadata(songId, title, artist, album)
        if (result is UpdateResult.Success) {
            invalidateCache()
        }
        return result
    }

    suspend fun deleteSong(songId: Long): DeleteResult {
        val result = mediaStoreScanner.deleteSong(songId)
        if (result is DeleteResult.Success) {
            invalidateCache()
        }
        return result
    }

    fun invalidateCache() {
        cachedSongs = emptyList()
    }
}

package com.example.liliplayer.data.repository

import com.example.liliplayer.data.local.dao.FavoriteDao
import com.example.liliplayer.data.local.dao.PlaylistDao
import com.example.liliplayer.data.local.dao.PlaylistSongDao
import com.example.liliplayer.data.local.entity.FavoriteEntity
import com.example.liliplayer.data.local.entity.PlaylistEntity
import com.example.liliplayer.data.local.entity.PlaylistSongCrossRef
import com.example.liliplayer.domain.model.Playlist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao,
    private val playlistSongDao: PlaylistSongDao,
    private val favoriteDao: FavoriteDao
) {
    fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                Playlist(
                    id = entity.id,
                    name = entity.name,
                    songCount = 0,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt
                )
            }
        }
    }

    suspend fun createPlaylist(name: String): Long {
        return playlistDao.insertPlaylist(PlaylistEntity(name = name))
    }

    suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylistById(playlistId)
    }

    suspend fun renamePlaylist(playlistId: Long, newName: String) {
        val playlist = playlistDao.getPlaylistById(playlistId) ?: return
        playlistDao.updatePlaylist(playlist.copy(name = newName, updatedAt = System.currentTimeMillis()))
    }

    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>> {
        return playlistSongDao.getSongIdsForPlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val maxOrder = playlistSongDao.getMaxSortOrder(playlistId) ?: -1
        playlistSongDao.addSongToPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = songId, sortOrder = maxOrder + 1)
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        playlistSongDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getFavoriteIds(): Flow<List<Long>> = favoriteDao.getAllFavoriteIds()

    fun isFavorite(songId: Long): Flow<Boolean> = favoriteDao.isFavorite(songId)

    suspend fun toggleFavorite(songId: Long) {
        if (favoriteDao.isFavoriteDirect(songId)) {
            favoriteDao.removeFavorite(songId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(songId = songId))
        }
    }

    suspend fun addFavorite(songId: Long) {
        favoriteDao.addFavorite(FavoriteEntity(songId = songId))
    }

    suspend fun removeFavorite(songId: Long) {
        favoriteDao.removeFavorite(songId)
    }
}

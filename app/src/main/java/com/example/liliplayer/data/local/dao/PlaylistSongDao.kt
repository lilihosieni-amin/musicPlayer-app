package com.example.liliplayer.data.local.dao

import androidx.room.*
import com.example.liliplayer.data.local.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistSongDao {
    @Query("SELECT songId FROM playlist_songs WHERE playlistId = :playlistId ORDER BY sortOrder ASC")
    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongCountForPlaylist(playlistId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun clearPlaylist(playlistId: Long)

    @Query("SELECT MAX(sortOrder) FROM playlist_songs WHERE playlistId = :playlistId")
    suspend fun getMaxSortOrder(playlistId: Long): Int?
}

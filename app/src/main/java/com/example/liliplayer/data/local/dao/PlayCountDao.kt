package com.example.liliplayer.data.local.dao

import androidx.room.*
import com.example.liliplayer.data.local.entity.PlayCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayCountDao {
    @Query("SELECT * FROM play_counts ORDER BY count DESC LIMIT 50")
    fun getMostPlayed(): Flow<List<PlayCountEntity>>

    @Query("SELECT * FROM play_counts WHERE songId = :songId")
    suspend fun getPlayCount(songId: Long): PlayCountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPlayCount(playCount: PlayCountEntity)
}

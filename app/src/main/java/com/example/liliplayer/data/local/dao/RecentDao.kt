package com.example.liliplayer.data.local.dao

import androidx.room.*
import com.example.liliplayer.data.local.entity.RecentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Query("SELECT songId FROM recents ORDER BY playedAt DESC LIMIT 50")
    fun getRecentSongIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(recent: RecentEntity)

    @Query("DELETE FROM recents")
    suspend fun clearRecents()
}

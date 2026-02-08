package com.example.liliplayer.data.local.dao

import androidx.room.*
import com.example.liliplayer.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT songId FROM favorites ORDER BY addedAt DESC")
    fun getAllFavoriteIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    fun isFavorite(songId: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavoriteDirect(songId: Long): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun removeFavorite(songId: Long)
}

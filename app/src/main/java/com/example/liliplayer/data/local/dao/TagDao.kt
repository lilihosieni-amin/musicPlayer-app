package com.example.liliplayer.data.local.dao

import androidx.room.*
import com.example.liliplayer.data.local.entity.TagEntity
import com.example.liliplayer.data.local.entity.TagSongCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<TagEntity>>

    @Query("SELECT * FROM tags WHERE id = :tagId")
    suspend fun getTagById(tagId: Long): TagEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTag(tag: TagEntity): Long

    @Update
    suspend fun updateTag(tag: TagEntity)

    @Query("DELETE FROM tags WHERE id = :tagId")
    suspend fun deleteTag(tagId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTagToSong(crossRef: TagSongCrossRef)

    @Query("DELETE FROM tag_songs WHERE tagId = :tagId AND songId = :songId")
    suspend fun removeTagFromSong(tagId: Long, songId: Long)

    @Query("SELECT tagId FROM tag_songs WHERE songId = :songId")
    fun getTagIdsForSong(songId: Long): Flow<List<Long>>

    @Query("SELECT songId FROM tag_songs WHERE tagId = :tagId")
    fun getSongIdsForTag(tagId: Long): Flow<List<Long>>

    @Query("SELECT COUNT(*) FROM tag_songs WHERE tagId = :tagId")
    fun getSongCountForTag(tagId: Long): Flow<Int>
}

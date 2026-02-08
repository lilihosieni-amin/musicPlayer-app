package com.example.liliplayer.data.repository

import com.example.liliplayer.data.local.dao.TagDao
import com.example.liliplayer.data.local.entity.TagEntity
import com.example.liliplayer.data.local.entity.TagSongCrossRef
import com.example.liliplayer.domain.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    private val tagDao: TagDao,
    private val songRepository: SongRepository
) {
    fun getAllTags(): Flow<List<TagEntity>> = tagDao.getAllTags()

    suspend fun getTagById(tagId: Long): TagEntity? = tagDao.getTagById(tagId)

    suspend fun createTag(name: String, color: String = "#6941C6"): Long {
        return tagDao.insertTag(TagEntity(name = name, color = color))
    }

    suspend fun deleteTag(tagId: Long) {
        tagDao.deleteTag(tagId)
    }

    suspend fun updateTag(tag: TagEntity) {
        tagDao.updateTag(tag)
    }

    suspend fun addTagToSong(tagId: Long, songId: Long) {
        tagDao.addTagToSong(TagSongCrossRef(tagId = tagId, songId = songId))
    }

    suspend fun removeTagFromSong(tagId: Long, songId: Long) {
        tagDao.removeTagFromSong(tagId, songId)
    }

    fun getTagIdsForSong(songId: Long): Flow<List<Long>> = tagDao.getTagIdsForSong(songId)

    fun getSongIdsForTag(tagId: Long): Flow<List<Long>> = tagDao.getSongIdsForTag(tagId)

    fun getSongCountForTag(tagId: Long): Flow<Int> = tagDao.getSongCountForTag(tagId)

    suspend fun getSongsForTag(tagId: Long): List<Song> {
        val songIds = tagDao.getSongIdsForTag(tagId).firstOrNull() ?: emptyList()
        return songRepository.getSongsByIds(songIds)
    }

    fun getTagsForSong(songId: Long): Flow<List<TagEntity>> {
        return tagDao.getTagIdsForSong(songId).map { tagIds ->
            tagIds.mapNotNull { tagDao.getTagById(it) }
        }
    }
}

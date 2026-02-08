package com.example.liliplayer.ui.screens.tags

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.local.entity.TagEntity
import com.example.liliplayer.data.repository.TagRepository
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    private val tagRepository: TagRepository
) : ViewModel() {

    val tags: StateFlow<List<TagEntity>> = tagRepository.getAllTags()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _tagSongs = MutableStateFlow<List<Song>>(emptyList())
    val tagSongs: StateFlow<List<Song>> = _tagSongs.asStateFlow()

    private val _selectedTag = MutableStateFlow<TagEntity?>(null)
    val selectedTag: StateFlow<TagEntity?> = _selectedTag.asStateFlow()

    fun createTag(name: String, color: String = "#6941C6") {
        viewModelScope.launch {
            tagRepository.createTag(name, color)
        }
    }

    fun deleteTag(tagId: Long) {
        viewModelScope.launch {
            tagRepository.deleteTag(tagId)
        }
    }

    fun loadTagDetail(tagId: Long) {
        viewModelScope.launch {
            _selectedTag.value = tagRepository.getTagById(tagId)
            tagRepository.getSongIdsForTag(tagId).collect { songIds ->
                _tagSongs.value = tagRepository.getSongsForTag(tagId)
            }
        }
    }
}

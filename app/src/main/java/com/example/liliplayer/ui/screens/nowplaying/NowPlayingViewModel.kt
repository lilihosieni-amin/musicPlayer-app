package com.example.liliplayer.ui.screens.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.LyricsRepository
import com.example.liliplayer.data.repository.PlaylistRepository
import com.example.liliplayer.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val lyricsRepository: LyricsRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    private val _currentSongId = MutableStateFlow<Long?>(null)

    val isFavorite: StateFlow<Boolean> = _currentSongId.flatMapLatest { songId ->
        if (songId != null) playlistRepository.isFavorite(songId)
        else flowOf(false)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val songTagIds: StateFlow<List<Long>> = _currentSongId.flatMapLatest { songId ->
        if (songId != null) tagRepository.getTagIdsForSong(songId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _lyrics = MutableStateFlow<String?>(null)
    val lyrics: StateFlow<String?> = _lyrics.asStateFlow()

    private val _lyricsLoading = MutableStateFlow(false)
    val lyricsLoading: StateFlow<Boolean> = _lyricsLoading.asStateFlow()

    fun setSongId(songId: Long?) {
        _currentSongId.value = songId
    }

    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            playlistRepository.toggleFavorite(songId)
        }
    }

    fun toggleTagForSong(tagId: Long, songId: Long, add: Boolean) {
        viewModelScope.launch {
            if (add) tagRepository.addTagToSong(tagId, songId)
            else tagRepository.removeTagFromSong(tagId, songId)
        }
    }

    fun loadLyrics(artist: String, title: String, contentUri: android.net.Uri?) {
        viewModelScope.launch {
            _lyricsLoading.value = true
            _lyrics.value = lyricsRepository.getLyrics(artist, title, contentUri)
            _lyricsLoading.value = false
        }
    }

    fun clearLyrics() {
        _lyrics.value = null
    }
}

package com.example.liliplayer.ui.screens.shared

import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.mediastore.DeleteResult
import com.example.liliplayer.data.mediastore.UpdateResult
import com.example.liliplayer.data.repository.PlaylistRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.data.repository.TagRepository
import com.example.liliplayer.domain.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongActionsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    sealed class DeleteState {
        data class RequiresPermission(val intentSender: IntentSender) : DeleteState()
        data object Success : DeleteState()
        data class Error(val message: String) : DeleteState()
    }

    sealed class UpdateState {
        data class RequiresPermission(val intentSender: IntentSender) : UpdateState()
        data object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _addToPlaylistResult = MutableStateFlow<String?>(null)
    val addToPlaylistResult: StateFlow<String?> = _addToPlaylistResult.asStateFlow()

    private val _deleteResult = MutableStateFlow<DeleteState?>(null)
    val deleteResult: StateFlow<DeleteState?> = _deleteResult.asStateFlow()

    private val _updateResult = MutableStateFlow<UpdateState?>(null)
    val updateResult: StateFlow<UpdateState?> = _updateResult.asStateFlow()

    private var pendingDeleteSongId: Long? = null

    fun addSongToPlaylist(songId: Long, playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.addSongToPlaylist(playlistId, songId)
            val playlistName = playlists.value.find { it.id == playlistId }?.name ?: "playlist"
            _addToPlaylistResult.value = "Added to $playlistName"
        }
    }

    fun createPlaylistAndAddSong(name: String, songId: Long) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            playlistRepository.addSongToPlaylist(playlistId, songId)
            _addToPlaylistResult.value = "Added to $name"
        }
    }

    fun deleteSong(songId: Long) {
        pendingDeleteSongId = songId
        viewModelScope.launch {
            when (val result = songRepository.deleteSong(songId)) {
                is DeleteResult.Success -> {
                    _deleteResult.value = DeleteState.Success
                }
                is DeleteResult.RequiresPermission -> {
                    _deleteResult.value = DeleteState.RequiresPermission(result.intentSender)
                }
                is DeleteResult.Error -> {
                    _deleteResult.value = DeleteState.Error(result.message)
                }
            }
        }
    }

    fun updateSongMetadata(songId: Long, title: String, artist: String, album: String) {
        viewModelScope.launch {
            when (val result = songRepository.updateSongMetadata(songId, title, artist, album)) {
                is UpdateResult.Success -> {
                    _updateResult.value = UpdateState.Success
                }
                is UpdateResult.RequiresPermission -> {
                    _updateResult.value = UpdateState.RequiresPermission(result.intentSender)
                }
                is UpdateResult.Error -> {
                    _updateResult.value = UpdateState.Error(result.message)
                }
            }
        }
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

    fun onDeletePermissionGranted() {
        songRepository.invalidateCache()
        _deleteResult.value = DeleteState.Success
    }

    fun clearAddResult() {
        _addToPlaylistResult.value = null
    }

    fun clearDeleteResult() {
        _deleteResult.value = null
        pendingDeleteSongId = null
    }

    fun clearUpdateResult() {
        _updateResult.value = null
    }
}

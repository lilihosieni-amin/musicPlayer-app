package com.example.liliplayer.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.PlaylistRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Playlist
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistRepository: PlaylistRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _playlistSongs = MutableStateFlow<List<Song>>(emptyList())
    val playlistSongs: StateFlow<List<Song>> = _playlistSongs.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<Playlist?>(null)
    val selectedPlaylist: StateFlow<Playlist?> = _selectedPlaylist.asStateFlow()

    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.createPlaylist(name)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deletePlaylist(playlistId)
        }
    }

    fun renamePlaylist(playlistId: Long, newName: String) {
        viewModelScope.launch {
            playlistRepository.renamePlaylist(playlistId, newName)
        }
    }

    fun loadPlaylistDetail(playlistId: Long) {
        viewModelScope.launch {
            _selectedPlaylist.value = playlists.value.find { it.id == playlistId }
            playlistRepository.getSongIdsForPlaylist(playlistId).collect { songIds ->
                _playlistSongs.value = songRepository.getSongsByIds(songIds)
            }
        }
    }

    fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        viewModelScope.launch {
            playlistRepository.removeSongFromPlaylist(playlistId, songId)
        }
    }
}

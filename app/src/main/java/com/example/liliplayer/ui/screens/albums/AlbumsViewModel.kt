package com.example.liliplayer.ui.screens.albums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.AlbumRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Album
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // For album detail
    private val _selectedAlbum = MutableStateFlow<Album?>(null)
    val selectedAlbum: StateFlow<Album?> = _selectedAlbum.asStateFlow()

    private val _albumSongs = MutableStateFlow<List<Song>>(emptyList())
    val albumSongs: StateFlow<List<Song>> = _albumSongs.asStateFlow()

    fun loadAlbums() {
        viewModelScope.launch {
            _isLoading.value = true
            _albums.value = albumRepository.getAllAlbums()
            _isLoading.value = false
        }
    }

    fun loadAlbumDetail(albumId: Long) {
        viewModelScope.launch {
            _selectedAlbum.value = albumRepository.getAlbumById(albumId)
            _albumSongs.value = songRepository.getSongsForAlbum(albumId)
        }
    }
}

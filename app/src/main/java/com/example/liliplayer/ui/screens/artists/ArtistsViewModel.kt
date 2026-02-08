package com.example.liliplayer.ui.screens.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.AlbumRepository
import com.example.liliplayer.data.repository.ArtistRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Album
import com.example.liliplayer.domain.model.Artist
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val songRepository: SongRepository
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedArtist = MutableStateFlow<Artist?>(null)
    val selectedArtist: StateFlow<Artist?> = _selectedArtist.asStateFlow()

    private val _artistAlbums = MutableStateFlow<List<Album>>(emptyList())
    val artistAlbums: StateFlow<List<Album>> = _artistAlbums.asStateFlow()

    private val _artistSongs = MutableStateFlow<List<Song>>(emptyList())
    val artistSongs: StateFlow<List<Song>> = _artistSongs.asStateFlow()

    fun loadArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            _artists.value = artistRepository.getAllArtists()
            _isLoading.value = false
        }
    }

    fun loadArtistDetail(artistId: Long) {
        viewModelScope.launch {
            _selectedArtist.value = artistRepository.getArtistById(artistId)
            _artistSongs.value = songRepository.getSongsForArtist(artistId)
            _artistAlbums.value = albumRepository.getAlbumsForArtist(artistId)
        }
    }
}

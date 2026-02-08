package com.example.liliplayer.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.AlbumRepository
import com.example.liliplayer.data.repository.ArtistRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Album
import com.example.liliplayer.domain.model.Artist
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
        searchJob?.cancel()
        if (newQuery.isBlank()) {
            _songs.value = emptyList()
            _albums.value = emptyList()
            _artists.value = emptyList()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            _songs.value = songRepository.searchSongs(newQuery)
            _albums.value = albumRepository.searchAlbums(newQuery)
            _artists.value = artistRepository.searchArtists(newQuery)
        }
    }
}

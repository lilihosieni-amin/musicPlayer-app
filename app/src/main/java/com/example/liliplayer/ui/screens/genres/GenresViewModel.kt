package com.example.liliplayer.ui.screens.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.repository.GenreRepository
import com.example.liliplayer.domain.model.Genre
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresViewModel @Inject constructor(
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val _genres = MutableStateFlow<List<Genre>>(emptyList())
    val genres: StateFlow<List<Genre>> = _genres.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedGenre = MutableStateFlow<Genre?>(null)
    val selectedGenre: StateFlow<Genre?> = _selectedGenre.asStateFlow()

    private val _genreSongs = MutableStateFlow<List<Song>>(emptyList())
    val genreSongs: StateFlow<List<Song>> = _genreSongs.asStateFlow()

    fun loadGenres() {
        viewModelScope.launch {
            _isLoading.value = true
            _genres.value = genreRepository.getAllGenres().filter { it.songCount > 0 }
            _isLoading.value = false
        }
    }

    fun loadGenreDetail(genreId: Long) {
        viewModelScope.launch {
            _selectedGenre.value = genreRepository.getGenreById(genreId)
            _genreSongs.value = genreRepository.getSongsForGenre(genreId)
        }
    }
}

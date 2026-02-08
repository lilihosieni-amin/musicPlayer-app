package com.example.liliplayer.ui.screens.songs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.datastore.SettingsDataStore
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.domain.model.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())

    private val _filterQuery = MutableStateFlow("")
    val filterQuery: StateFlow<String> = _filterQuery.asStateFlow()

    val songs: StateFlow<List<Song>> = combine(_allSongs, _filterQuery) { songs, query ->
        if (query.isBlank()) songs
        else {
            val lower = query.lowercase()
            songs.filter {
                it.title.lowercase().contains(lower) ||
                it.artist.lowercase().contains(lower) ||
                it.album.lowercase().contains(lower)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val sortOrder: StateFlow<SortOrder> = settingsDataStore.sortOrder
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SortOrder.TITLE_ASC)

    init {
        viewModelScope.launch {
            settingsDataStore.sortOrder.collect { order ->
                loadSongs(order)
            }
        }
    }

    private suspend fun loadSongs(sortOrder: SortOrder) {
        _isLoading.value = true
        _allSongs.value = songRepository.getSongsSorted(sortOrder)
        _isLoading.value = false
    }

    fun setFilterQuery(query: String) {
        _filterQuery.value = query
    }

    fun setSortOrder(order: SortOrder) {
        viewModelScope.launch {
            settingsDataStore.setSortOrder(order)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            songRepository.invalidateCache()
            loadSongs(sortOrder.value)
        }
    }
}

package com.example.liliplayer.ui.screens.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liliplayer.data.local.dao.PlayCountDao
import com.example.liliplayer.data.local.dao.RecentDao
import com.example.liliplayer.data.repository.PlaylistRepository
import com.example.liliplayer.data.repository.SongRepository
import com.example.liliplayer.domain.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmartPlaylistViewModel @Inject constructor(
    private val songRepository: SongRepository,
    private val playlistRepository: PlaylistRepository,
    private val recentDao: RecentDao,
    private val playCountDao: PlayCountDao
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()

    fun loadSmartPlaylist(type: String) {
        viewModelScope.launch {
            _songs.value = when (type) {
                "favorites" -> {
                    val ids = playlistRepository.getFavoriteIds().firstOrNull() ?: emptyList()
                    songRepository.getSongsByIds(ids)
                }
                "most_played" -> {
                    val entities = playCountDao.getMostPlayed().firstOrNull() ?: emptyList()
                    val ids = entities.map { it.songId }
                    songRepository.getSongsByIds(ids)
                }
                "recently_played" -> {
                    val ids = recentDao.getRecentSongIds().firstOrNull() ?: emptyList()
                    songRepository.getSongsByIds(ids)
                }
                "recently_added" -> {
                    songRepository.getAllSongs()
                        .sortedByDescending { it.dateAdded }
                        .take(50)
                }
                else -> emptyList()
            }
        }
    }
}

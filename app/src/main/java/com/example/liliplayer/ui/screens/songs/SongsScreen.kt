package com.example.liliplayer.ui.screens.songs

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.DeleteConfirmationDialog
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.components.PermissionHandler
import com.example.liliplayer.ui.components.PlaylistPickerDialog
import com.example.liliplayer.ui.components.SongListItem
import com.example.liliplayer.ui.components.SortMenuDropdown
import com.example.liliplayer.ui.screens.shared.SongActionsViewModel
import com.example.liliplayer.ui.theme.*

@Composable
fun SongsScreen(
    playbackController: PlaybackController,
    onNavigateToNowPlaying: () -> Unit,
    viewModel: SongsViewModel = hiltViewModel(),
    songActionsViewModel: SongActionsViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sortOrder by viewModel.sortOrder.collectAsState()
    val playbackState by playbackController.playbackState.collectAsState()

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val addResult by songActionsViewModel.addToPlaylistResult.collectAsState()
    val deleteResult by songActionsViewModel.deleteResult.collectAsState()

    val listState = rememberLazyListState()

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            songActionsViewModel.onDeletePermissionGranted()
            viewModel.refresh()
        }
    }

    LaunchedEffect(addResult) {
        addResult?.let {
            snackbarHostState.showSnackbar(it)
            songActionsViewModel.clearAddResult()
        }
    }

    LaunchedEffect(deleteResult) {
        deleteResult?.let { result ->
            when (result) {
                is SongActionsViewModel.DeleteState.RequiresPermission -> {
                    deleteLauncher.launch(IntentSenderRequest.Builder(result.intentSender).build())
                }
                is SongActionsViewModel.DeleteState.Success -> {
                    snackbarHostState.showSnackbar("Song deleted")
                    songActionsViewModel.clearDeleteResult()
                    viewModel.refresh()
                }
                is SongActionsViewModel.DeleteState.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                    songActionsViewModel.clearDeleteResult()
                }
            }
        }
    }

    PermissionHandler {
        LaunchedEffect(Unit) { viewModel.refresh() }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { scaffoldPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(scaffoldPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${songs.size} songs",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    SortMenuDropdown(
                        currentSort = sortOrder,
                        onSortSelected = { viewModel.setSortOrder(it) }
                    )
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                            color = Primary
                        )
                    }
                } else if (songs.isEmpty()) {
                    EmptyState(message = "No songs found")
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(songs, key = { it.id }) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = playbackState.currentSong?.id == song.id,
                                onClick = {
                                    playbackController.playSongs(songs, songs.indexOf(song))
                                    onNavigateToNowPlaying()
                                },
                                onAddToQueue = { playbackController.addToQueue(song) },
                                onAddToPlaylist = { songToAddToPlaylist = song },
                                onDelete = { songToDelete = song }
                            )
                        }
                    }
                }
            }
        }
    }

    songToAddToPlaylist?.let { song ->
        PlaylistPickerDialog(
            onDismiss = { songToAddToPlaylist = null },
            onPlaylistSelected = { playlistId ->
                songActionsViewModel.addSongToPlaylist(song.id, playlistId)
                songToAddToPlaylist = null
            },
            onCreatePlaylist = { name ->
                songActionsViewModel.createPlaylistAndAddSong(name, song.id)
                songToAddToPlaylist = null
            }
        )
    }

    songToDelete?.let { song ->
        DeleteConfirmationDialog(
            songTitle = song.title,
            onConfirm = {
                songActionsViewModel.deleteSong(song.id)
                songToDelete = null
            },
            onDismiss = { songToDelete = null }
        )
    }
}

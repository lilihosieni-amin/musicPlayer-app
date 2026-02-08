package com.example.liliplayer.ui.screens.artists

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.AlbumGridItem
import com.example.liliplayer.ui.components.DeleteConfirmationDialog
import com.example.liliplayer.ui.components.PlaylistPickerDialog
import com.example.liliplayer.ui.components.SongListItem
import com.example.liliplayer.ui.screens.shared.SongActionsViewModel
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatAlbumCount
import com.example.liliplayer.util.formatSongCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistId: Long,
    playbackController: PlaybackController,
    onBack: () -> Unit,
    onAlbumClick: (Long) -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel(),
    songActionsViewModel: SongActionsViewModel = hiltViewModel()
) {
    val artist by viewModel.selectedArtist.collectAsState()
    val albums by viewModel.artistAlbums.collectAsState()
    val songs by viewModel.artistSongs.collectAsState()
    val playbackState by playbackController.playbackState.collectAsState()

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val addResult by songActionsViewModel.addToPlaylistResult.collectAsState()
    val deleteResult by songActionsViewModel.deleteResult.collectAsState()

    val deleteLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            songActionsViewModel.onDeletePermissionGranted()
            viewModel.loadArtistDetail(artistId)
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
                    viewModel.loadArtistDetail(artistId)
                }
                is SongActionsViewModel.DeleteState.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                    songActionsViewModel.clearDeleteResult()
                }
            }
        }
    }

    LaunchedEffect(artistId) { viewModel.loadArtistDetail(artistId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(artist?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgMain)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(BlueLight, MintLight)
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = artist?.name ?: "",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${artist?.albumCount?.formatAlbumCount() ?: "0 albums"} · ${artist?.songCount?.formatSongCount() ?: "0 songs"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    if (songs.isNotEmpty()) {
                                        playbackController.playSongs(songs)
                                        onNavigateToNowPlaying()
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Primary,
                                    contentColor = BgCard
                                )
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play All")
                            }
                            Button(
                                onClick = {
                                    if (songs.isNotEmpty()) {
                                        playbackController.playSongs(songs.shuffled())
                                        onNavigateToNowPlaying()
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Secondary,
                                    contentColor = BgCard
                                )
                            ) {
                                Icon(Icons.Default.Shuffle, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Shuffle")
                            }
                        }
                    }
                }
            }

            if (albums.isNotEmpty()) {
                item {
                    Text(
                        text = "Albums",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(albums, key = { it.id }) { album ->
                            Box(modifier = Modifier.width(160.dp)) {
                                AlbumGridItem(
                                    album = album,
                                    onClick = { onAlbumClick(album.id) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Songs",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
            }

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

package com.example.liliplayer.ui.screens.search

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.AlbumGridItem
import com.example.liliplayer.ui.components.ArtistListItem
import com.example.liliplayer.ui.components.DeleteConfirmationDialog
import com.example.liliplayer.ui.components.PlaylistPickerDialog
import com.example.liliplayer.ui.components.SongListItem
import com.example.liliplayer.ui.screens.shared.SongActionsViewModel
import com.example.liliplayer.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    playbackController: PlaybackController,
    onBack: () -> Unit,
    onNavigateToAlbum: (Long) -> Unit,
    onNavigateToArtist: (Long) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    songActionsViewModel: SongActionsViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
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
                }
                is SongActionsViewModel.DeleteState.Error -> {
                    snackbarHostState.showSnackbar(result.message)
                    songActionsViewModel.clearDeleteResult()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.onQueryChange(it) },
                        placeholder = { Text("Search songs, albums, artists...") },
                        singleLine = true,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (query.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onQueryChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderDark.copy(alpha = 0.2f),
                            focusedContainerColor = BgCard,
                            unfocusedContainerColor = BgCard
                        )
                    )
                },
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
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (artists.isNotEmpty()) {
                item {
                    Text(
                        text = "Artists",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(artists.take(5), key = { "artist_${it.id}" }) { artist ->
                    ArtistListItem(
                        artist = artist,
                        onClick = { onNavigateToArtist(artist.id) }
                    )
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
                items(albums.take(5), key = { "album_${it.id}" }) { album ->
                    AlbumGridItem(
                        album = album,
                        onClick = { onNavigateToAlbum(album.id) }
                    )
                }
            }

            if (songs.isNotEmpty()) {
                item {
                    Text(
                        text = "Songs",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                items(songs, key = { "song_${it.id}" }) { song ->
                    SongListItem(
                        song = song,
                        isPlaying = playbackState.currentSong?.id == song.id,
                        onClick = {
                            playbackController.playSongs(songs, songs.indexOf(song))
                        },
                        onAddToQueue = { playbackController.addToQueue(song) },
                        onAddToPlaylist = { songToAddToPlaylist = song },
                        onDelete = { songToDelete = song }
                    )
                }
            }

            if (query.isNotEmpty() && songs.isEmpty() && albums.isEmpty() && artists.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = "No results found for \"$query\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
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

package com.example.liliplayer.ui.screens.tags

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.components.SongListItem
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatSongCount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagDetailScreen(
    tagId: Long,
    playbackController: PlaybackController,
    onBack: () -> Unit,
    onNavigateToNowPlaying: () -> Unit,
    viewModel: TagsViewModel = hiltViewModel()
) {
    val tag by viewModel.selectedTag.collectAsState()
    val songs by viewModel.tagSongs.collectAsState()
    val playbackState by playbackController.playbackState.collectAsState()

    LaunchedEffect(tagId) { viewModel.loadTagDetail(tagId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(tag?.name ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgMain)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(colors = listOf(LavenderLight, BlueLight)))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(tag?.name ?: "", style = MaterialTheme.typography.headlineMedium)
                        Text(songs.size.formatSongCount(), style = MaterialTheme.typography.bodyMedium)
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
                                colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = TextPrimary),
                                enabled = songs.isNotEmpty()
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play All")
                            }
                            OutlinedButton(
                                onClick = {
                                    if (songs.isNotEmpty()) {
                                        playbackController.playSongs(songs.shuffled())
                                        onNavigateToNowPlaying()
                                    }
                                },
                                shape = RoundedCornerShape(20.dp),
                                enabled = songs.isNotEmpty()
                            ) {
                                Icon(Icons.Default.Shuffle, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Shuffle")
                            }
                        }
                    }
                }
            }

            if (songs.isEmpty()) {
                item { EmptyState(message = "No songs with this tag") }
            } else {
                items(songs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        isPlaying = playbackState.currentSong?.id == song.id,
                        onClick = {
                            playbackController.playSongs(songs, songs.indexOf(song))
                            onNavigateToNowPlaying()
                        },
                        onAddToQueue = { playbackController.addToQueue(song) }
                    )
                }
            }
        }
    }
}

package com.example.liliplayer.ui.screens.queue

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.AlbumArtImage
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueueScreen(
    playbackController: PlaybackController,
    onBack: () -> Unit
) {
    val playbackState by playbackController.playbackState.collectAsState()
    val queue = playbackState.queue

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Queue (${queue.size} songs)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgMain)
            )
        }
    ) { innerPadding ->
        if (queue.isEmpty()) {
            EmptyState(
                message = "Queue is empty",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                itemsIndexed(queue, key = { index, song -> "${song.id}_$index" }) { index, song ->
                    val isCurrent = index == playbackState.currentIndex

                    val glowColors = listOf(MintGreen, SoftPink, SkyBlue, Lavender, SunnyYellow)
                    val glowColor = if (isCurrent) glowColors[(index % glowColors.size)] else BorderDark

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .shadow(
                                elevation = if (isCurrent) 12.dp else 6.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = glowColor.copy(alpha = 0.4f),
                                spotColor = glowColor.copy(alpha = 0.3f)
                            ),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) LavenderLight else BgCard
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DragHandle,
                                contentDescription = "Drag",
                                tint = TextSecondary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            AlbumArtImage(uri = song.albumArtUri)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (isCurrent) Primary else TextPrimary
                                )
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = song.duration.formatDuration(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            IconButton(
                                onClick = { playbackController.removeFromQueue(index) }
                            ) {
                                Icon(
                                    Icons.Default.RemoveCircleOutline,
                                    contentDescription = "Remove",
                                    tint = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

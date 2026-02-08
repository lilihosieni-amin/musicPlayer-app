package com.example.liliplayer.ui.screens.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.liliplayer.domain.model.RepeatMode
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playbackController: PlaybackController,
    onBack: () -> Unit,
    onNavigateToQueue: () -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: ((Long) -> Unit)? = null,
    onAddToPlaylist: ((Song) -> Unit)? = null,
    onManageTags: ((Song) -> Unit)? = null,
    onEditMetadata: ((Song) -> Unit)? = null,
    onDelete: ((Song) -> Unit)? = null,
    lyrics: String? = null,
    lyricsLoading: Boolean = false,
    onLoadLyrics: ((Song) -> Unit)? = null,
    onClearLyrics: (() -> Unit)? = null
) {
    val playbackState by playbackController.playbackState.collectAsState()
    val song = playbackState.currentSong

    var isDragging by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var showLyrics by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgMain)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "PLAYING NOW",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = "More", tint = TextPrimary, modifier = Modifier.size(24.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, shape = RoundedCornerShape(20.dp)) {
                        DropdownMenuItem(text = { Text("Queue") }, onClick = { showMenu = false; onNavigateToQueue() }, leadingIcon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, null, tint = SkyBlue) })
                        if (song != null && onAddToPlaylist != null) {
                            DropdownMenuItem(text = { Text("Add to Playlist") }, onClick = { showMenu = false; onAddToPlaylist(song) }, leadingIcon = { Icon(Icons.Default.PlaylistAdd, null, tint = MintGreen) })
                        }
                        if (song != null && onManageTags != null) {
                            DropdownMenuItem(text = { Text("Manage Tags") }, onClick = { showMenu = false; onManageTags(song) }, leadingIcon = { Icon(Icons.Default.Label, null, tint = Lavender) })
                        }
                        if (song != null && onEditMetadata != null) {
                            DropdownMenuItem(text = { Text("Edit Info") }, onClick = { showMenu = false; onEditMetadata(song) }, leadingIcon = { Icon(Icons.Default.Edit, null, tint = SunnyYellow) })
                        }
                        if (song != null && onDelete != null) {
                            DropdownMenuItem(text = { Text("Delete", color = ErrorRed) }, onClick = { showMenu = false; onDelete(song) }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = ErrorRed) })
                        }
                    }
                }
            }

            if (song == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No song playing", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.weight(0.03f))

                    // Album art OR lyrics
                    if (showLyrics) {
                        Card(
                            modifier = Modifier.weight(0.45f).fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = LavenderLight.copy(alpha = 0.4f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            if (lyricsLoading) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Primary)
                                }
                            } else if (lyrics != null) {
                                Text(
                                    text = lyrics,
                                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 26.sp),
                                    color = TextPrimary
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("No lyrics found", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                                }
                            }
                        }
                    } else {
                        Box(modifier = Modifier.weight(0.45f), contentAlignment = Alignment.Center) {
                            AsyncImage(
                                model = song.albumArtUri,
                                contentDescription = "Album art",
                                modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .aspectRatio(1f)
                                    .shadow(20.dp, CircleShape, ambientColor = Primary.copy(alpha = 0.2f), spotColor = Secondary.copy(alpha = 0.15f))
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.03f))

                    // Song info + heart
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, fontSize = 22.sp),
                                maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextPrimary
                            )
                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                        }
                        if (onToggleFavorite != null) {
                            IconButton(onClick = { onToggleFavorite(song.id) }) {
                                Icon(
                                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite",
                                    tint = if (isFavorite) Secondary else TextSecondary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Thick gradient seek bar
                    Column {
                        val displayPosition = if (isDragging) sliderPosition
                        else if (playbackState.duration > 0) playbackState.position.toFloat() / playbackState.duration.toFloat() else 0f

                        // Custom thick rounded progress track
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SeekBarTrack)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(displayPosition.coerceIn(0f, 1f))
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Brush.horizontalGradient(colors = listOf(Primary, Secondary, Accent)))
                            )
                        }
                        // Invisible slider on top for dragging
                        Slider(
                            value = displayPosition,
                            onValueChange = { isDragging = true; sliderPosition = it },
                            onValueChangeFinished = {
                                playbackController.seekTo((sliderPosition * playbackState.duration).toLong())
                                isDragging = false
                            },
                            modifier = Modifier.fillMaxWidth().height(24.dp).offset(y = (-16).dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = androidx.compose.ui.graphics.Color.Transparent,
                                inactiveTrackColor = androidx.compose.ui.graphics.Color.Transparent
                            )
                        )
                        Row(modifier = Modifier.fillMaxWidth().offset(y = (-12).dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            val currentMs = if (isDragging) (sliderPosition * playbackState.duration).toLong() else playbackState.position
                            val remainingMs = playbackState.duration - currentMs
                            Text(currentMs.formatDuration(), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            Text("-${remainingMs.formatDuration()}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }

                    // Transport controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { playbackController.toggleRepeat() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                when (playbackState.repeatMode) { RepeatMode.ONE -> Icons.Default.RepeatOne; else -> Icons.Default.Repeat },
                                contentDescription = "Repeat",
                                tint = if (playbackState.repeatMode != RepeatMode.OFF) Accent else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        IconButton(onClick = { playbackController.previous() }, modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(36.dp), tint = Primary)
                        }
                        // Gradient play/pause button
                        Surface(
                            onClick = { playbackController.playPause() },
                            modifier = Modifier
                                .size(72.dp)
                                .shadow(12.dp, CircleShape, ambientColor = Primary.copy(alpha = 0.3f), spotColor = Secondary.copy(alpha = 0.2f)),
                            shape = CircleShape,
                            color = Primary
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.linearGradient(colors = listOf(Primary, Secondary)), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                                    modifier = Modifier.size(38.dp),
                                    tint = BgCard
                                )
                            }
                        }
                        IconButton(onClick = { playbackController.next() }, modifier = Modifier.size(52.dp)) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(36.dp), tint = Secondary)
                        }
                        IconButton(onClick = { playbackController.toggleShuffle() }, modifier = Modifier.size(40.dp)) {
                            Icon(
                                Icons.Default.Shuffle, contentDescription = "Shuffle",
                                tint = if (playbackState.shuffleEnabled) Accent else TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(0.04f))

                    // LYRICS button
                    if (onLoadLyrics != null) {
                        Surface(
                            onClick = {
                                if (showLyrics) { showLyrics = false; onClearLyrics?.invoke() }
                                else { showLyrics = true; onLoadLyrics(song) }
                            },
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                                .shadow(8.dp, RoundedCornerShape(22.dp), ambientColor = if (showLyrics) Primary.copy(alpha = 0.3f) else SkyBlue.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(22.dp),
                            color = if (showLyrics) Primary else MintLight
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    "LYRICS",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = if (showLyrics) BgCard else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

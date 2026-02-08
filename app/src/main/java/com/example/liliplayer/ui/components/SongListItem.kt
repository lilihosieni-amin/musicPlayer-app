package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatDuration

private val cardColors = listOf(MintLight, PinkLight, BlueLight, LavenderLight, YellowLight)
private val glowColors = listOf(MintGreen, SoftPink, SkyBlue, Lavender, SunnyYellow)

@Composable
fun SongListItem(
    song: Song,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    onClick: () -> Unit,
    onAddToPlaylist: (() -> Unit)? = null,
    onAddToQueue: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onToggleFavorite: (() -> Unit)? = null,
    onRemoveFromPlaylist: (() -> Unit)? = null,
    onEditInfo: (() -> Unit)? = null,
    onManageTags: (() -> Unit)? = null
) {
    var showMenu by remember { mutableStateOf(false) }
    val colorIndex = (song.id % cardColors.size).toInt()
    val tintColor = if (isPlaying) cardColors[colorIndex] else BgCard
    val glowColor = if (isPlaying) glowColors[colorIndex] else Color(0xFFD8D8E0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .shadow(
                elevation = if (isPlaying) 12.dp else 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = glowColor.copy(alpha = 0.4f),
                spotColor = glowColor.copy(alpha = 0.3f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = tintColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = song.albumArtUri,
                contentDescription = "Album art",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (isFavorite) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorited",
                            tint = Secondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
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
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    shape = RoundedCornerShape(20.dp)
                ) {
                    if (onAddToQueue != null) {
                        DropdownMenuItem(
                            text = { Text("Add to Queue") },
                            onClick = { showMenu = false; onAddToQueue() }
                        )
                    }
                    if (onAddToPlaylist != null) {
                        DropdownMenuItem(
                            text = { Text("Add to Playlist") },
                            onClick = { showMenu = false; onAddToPlaylist() }
                        )
                    }
                    if (onToggleFavorite != null) {
                        DropdownMenuItem(
                            text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
                            onClick = { showMenu = false; onToggleFavorite() }
                        )
                    }
                    if (onManageTags != null) {
                        DropdownMenuItem(
                            text = { Text("Manage Tags") },
                            onClick = { showMenu = false; onManageTags() }
                        )
                    }
                    if (onEditInfo != null) {
                        DropdownMenuItem(
                            text = { Text("Edit Info") },
                            onClick = { showMenu = false; onEditInfo() }
                        )
                    }
                    if (onRemoveFromPlaylist != null) {
                        DropdownMenuItem(
                            text = { Text("Remove from Playlist") },
                            onClick = { showMenu = false; onRemoveFromPlaylist() }
                        )
                    }
                    if (onDelete != null) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = ErrorRed) },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}

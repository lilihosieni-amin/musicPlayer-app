package com.example.liliplayer.ui.screens.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatSongCount

data class SmartPlaylistItem(
    val type: String,
    val label: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

@Composable
fun PlaylistsScreen(
    onPlaylistClick: (Long) -> Unit,
    onSmartPlaylistClick: (String) -> Unit = {},
    viewModel: PlaylistsViewModel = hiltViewModel()
) {
    val playlists by viewModel.playlists.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var showRenameDialog by remember { mutableStateOf<Long?>(null) }
    var renamePlaylistName by remember { mutableStateOf("") }

    val smartPlaylists = listOf(
        SmartPlaylistItem("favorites", "Favorites", Icons.Default.Favorite, SoftPink),
        SmartPlaylistItem("most_played", "Most Played", Icons.Default.TrendingUp, SunnyYellow),
        SmartPlaylistItem("recently_played", "Recently Played", Icons.Default.History, SkyBlue),
        SmartPlaylistItem("recently_added", "Recently Added", Icons.Default.NewReleases, MintGreen)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Secondary,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Playlist")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Smart playlists section
            item {
                Text(
                    "Smart Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(smartPlaylists) { smart ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(24.dp),
                            ambientColor = smart.color.copy(alpha = 0.4f),
                            spotColor = smart.color.copy(alpha = 0.3f)
                        )
                        .clickable { onSmartPlaylistClick(smart.type) },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = BgCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = smart.color
                        ) {
                            Icon(
                                smart.icon,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp),
                                tint = TextPrimary
                            )
                        }
                        Text(
                            text = smart.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        )
                    }
                }
            }

            // User playlists section
            item {
                Text(
                    "My Playlists",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (playlists.isEmpty()) {
                item {
                    EmptyState(message = "No playlists yet\nTap + to create one")
                }
            } else {
                items(playlists, key = { it.id }) { playlist ->
                    val glowColors = listOf(MintGreen, SoftPink, SkyBlue, Lavender, SunnyYellow)
                    val glowColor = glowColors[(playlist.id % glowColors.size).toInt()]
                    var showItemMenu by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp),
                                ambientColor = glowColor.copy(alpha = 0.4f),
                                spotColor = glowColor.copy(alpha = 0.3f)
                            )
                            .clickable { onPlaylistClick(playlist.id) },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = BgCard),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = glowColor
                            ) {
                                Icon(
                                    Icons.Default.QueueMusic,
                                    contentDescription = null,
                                    modifier = Modifier.padding(12.dp),
                                    tint = TextPrimary
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                            ) {
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = playlist.songCount.formatSongCount(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Box {
                                IconButton(onClick = { showItemMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = TextSecondary)
                                }
                                DropdownMenu(
                                    expanded = showItemMenu,
                                    onDismissRequest = { showItemMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename") },
                                        onClick = {
                                            showItemMenu = false
                                            renamePlaylistName = playlist.name
                                            showRenameDialog = playlist.id
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = ErrorRed) },
                                        onClick = {
                                            showItemMenu = false
                                            viewModel.deletePlaylist(playlist.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name ->
                viewModel.createPlaylist(name)
                showCreateDialog = false
            }
        )
    }

    showRenameDialog?.let { playlistId ->
        RenamePlaylistDialog(
            currentName = renamePlaylistName,
            onDismiss = { showRenameDialog = null },
            onRename = { newName ->
                viewModel.renamePlaylist(playlistId, newName)
                showRenameDialog = null
            }
        )
    }
}

@Composable
fun CreatePlaylistDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        title = {
            Text(
                "Create Playlist",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Playlist name") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onCreate(name.trim()) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Secondary,
                    contentColor = TextPrimary
                ),
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenamePlaylistDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        title = { Text("Rename Playlist", style = MaterialTheme.typography.headlineMedium) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Playlist name") },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onRename(name.trim()) },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Secondary, contentColor = TextPrimary),
                enabled = name.isNotBlank()
            ) { Text("Rename") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.screens.shared.SongActionsViewModel
import com.example.liliplayer.ui.theme.*

@Composable
fun PlaylistPickerDialog(
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit,
    onCreatePlaylist: (String) -> Unit,
    songActionsViewModel: SongActionsViewModel = hiltViewModel()
) {
    val playlists by songActionsViewModel.playlists.collectAsState()
    var showCreateField by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        containerColor = BgCard,
        title = { Text("Add to Playlist") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (showCreateField) {
                    OutlinedTextField(
                        value = newPlaylistName,
                        onValueChange = { newPlaylistName = it },
                        label = { Text("Playlist name") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = BorderDark.copy(alpha = 0.2f)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (newPlaylistName.isNotBlank()) {
                                onCreatePlaylist(newPlaylistName.trim())
                            }
                        },
                        enabled = newPlaylistName.isNotBlank(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Create & Add")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCreateField = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Create New Playlist", color = Primary)
                    }
                    HorizontalDivider()
                }

                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(playlists) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPlaylistSelected(playlist.id) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.QueueMusic,
                                contentDescription = null,
                                tint = TextSecondary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(playlist.name, color = TextPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

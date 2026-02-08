package com.example.liliplayer.ui.screens.artists

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.components.ArtistListItem
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.components.PermissionHandler

@Composable
fun ArtistsScreen(
    onArtistClick: (Long) -> Unit,
    viewModel: ArtistsViewModel = hiltViewModel()
) {
    val artists by viewModel.artists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    PermissionHandler {
        LaunchedEffect(Unit) { viewModel.loadArtists() }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = com.example.liliplayer.ui.theme.Primary
                )
            }
        } else if (artists.isEmpty()) {
            EmptyState(message = "No artists found")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(artists, key = { it.id }) { artist ->
                    ArtistListItem(
                        artist = artist,
                        onClick = { onArtistClick(artist.id) }
                    )
                }
            }
        }
    }
}

package com.example.liliplayer.ui.screens.albums

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.components.AlbumGridItem
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.components.PermissionHandler

@Composable
fun AlbumsScreen(
    onAlbumClick: (Long) -> Unit,
    viewModel: AlbumsViewModel = hiltViewModel()
) {
    val albums by viewModel.albums.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    PermissionHandler {
        LaunchedEffect(Unit) { viewModel.loadAlbums() }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = com.example.liliplayer.ui.theme.Primary
                )
            }
        } else if (albums.isEmpty()) {
            EmptyState(message = "No albums found")
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(albums, key = { it.id }) { album ->
                    AlbumGridItem(
                        album = album,
                        onClick = { onAlbumClick(album.id) }
                    )
                }
            }
        }
    }
}

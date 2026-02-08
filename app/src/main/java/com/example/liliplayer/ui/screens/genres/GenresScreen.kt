package com.example.liliplayer.ui.screens.genres

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.liliplayer.ui.components.EmptyState
import com.example.liliplayer.ui.components.GenreListItem
import com.example.liliplayer.ui.components.PermissionHandler

@Composable
fun GenresScreen(
    onGenreClick: (Long) -> Unit,
    viewModel: GenresViewModel = hiltViewModel()
) {
    val genres by viewModel.genres.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    PermissionHandler {
        LaunchedEffect(Unit) { viewModel.loadGenres() }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = com.example.liliplayer.ui.theme.Primary
                )
            }
        } else if (genres.isEmpty()) {
            EmptyState(message = "No genres found")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(genres, key = { it.id }) { genre ->
                    GenreListItem(
                        genre = genre,
                        onClick = { onGenreClick(genre.id) }
                    )
                }
            }
        }
    }
}

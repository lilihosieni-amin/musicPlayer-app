package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.liliplayer.domain.model.Album
import com.example.liliplayer.ui.theme.*

private val glowColors = listOf(MintGreen, SoftPink, SkyBlue, Lavender, SunnyYellow)

@Composable
fun AlbumGridItem(
    album: Album,
    onClick: () -> Unit
) {
    val glowColor = glowColors[(album.id % glowColors.size).toInt()]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = glowColor.copy(alpha = 0.4f),
                spotColor = glowColor.copy(alpha = 0.3f)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            AsyncImage(
                model = album.albumArtUri,
                contentDescription = album.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = album.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextPrimary
                )
                Text(
                    text = album.artist,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextSecondary
                )
            }
        }
    }
}

package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.liliplayer.domain.model.Artist
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatAlbumCount
import com.example.liliplayer.util.formatSongCount

private val artistGlowColors = listOf(Lavender, SoftPink, SkyBlue, MintGreen, SunnyYellow)

@Composable
fun ArtistListItem(
    artist: Artist,
    onClick: () -> Unit
) {
    val glowColor = artistGlowColors[(artist.id % artistGlowColors.size).toInt()]

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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BgCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = glowColor
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Primary
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = artist.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = "${artist.albumCount.formatAlbumCount()} · ${artist.songCount.formatSongCount()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

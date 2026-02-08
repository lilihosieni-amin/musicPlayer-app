package com.example.liliplayer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.liliplayer.domain.model.Genre
import com.example.liliplayer.ui.theme.*
import com.example.liliplayer.util.formatSongCount

private val genreGlowColors = listOf(MintGreen, SkyBlue, Lavender, SunnyYellow, SoftPink)

@Composable
fun GenreListItem(
    genre: Genre,
    onClick: () -> Unit
) {
    val glowColor = genreGlowColors[(genre.id % genreGlowColors.size).toInt()]

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
                    Icons.Default.Category,
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
                    text = genre.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Text(
                    text = genre.songCount.formatSongCount(),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

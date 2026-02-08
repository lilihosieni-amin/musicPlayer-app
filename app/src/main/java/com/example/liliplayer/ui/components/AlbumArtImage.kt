package com.example.liliplayer.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.liliplayer.ui.theme.*

@Composable
fun AlbumArtImage(
    uri: Uri?,
    size: Dp = 48.dp,
    cornerRadius: Dp = 20.dp
) {
    SubcomposeAsyncImage(
        model = uri,
        contentDescription = "Album art",
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius)),
        contentScale = ContentScale.Crop,
        error = {
            Surface(
                modifier = Modifier.size(size),
                shape = RoundedCornerShape(cornerRadius),
                color = LavenderLight
            ) {
                Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Primary
                )
            }
        },
        loading = {
            Surface(
                modifier = Modifier.size(size),
                shape = RoundedCornerShape(cornerRadius),
                color = LavenderLight
            ) {}
        }
    )
}

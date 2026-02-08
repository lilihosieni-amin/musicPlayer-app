package com.example.liliplayer.domain.model

import android.net.Uri

data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val artistId: Long,
    val songCount: Int,
    val year: Int,
    val albumArtUri: Uri?
)

package com.example.liliplayer.domain.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val artistId: Long,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val trackNumber: Int,
    val year: Int,
    val dateAdded: Long,
    val contentUri: Uri,
    val albumArtUri: Uri?
)

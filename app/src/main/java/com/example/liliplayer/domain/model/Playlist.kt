package com.example.liliplayer.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)

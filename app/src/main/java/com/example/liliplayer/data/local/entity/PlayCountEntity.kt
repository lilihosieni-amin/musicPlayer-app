package com.example.liliplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "play_counts")
data class PlayCountEntity(
    @PrimaryKey
    val songId: Long,
    val count: Int = 0,
    val lastPlayed: Long = System.currentTimeMillis()
)

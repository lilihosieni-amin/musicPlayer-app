package com.example.liliplayer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recents")
data class RecentEntity(
    @PrimaryKey
    val songId: Long,
    val playedAt: Long = System.currentTimeMillis()
)

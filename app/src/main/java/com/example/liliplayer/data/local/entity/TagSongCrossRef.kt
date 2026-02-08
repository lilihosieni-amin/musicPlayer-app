package com.example.liliplayer.data.local.entity

import androidx.room.Entity

@Entity(tableName = "tag_songs", primaryKeys = ["tagId", "songId"])
data class TagSongCrossRef(
    val tagId: Long,
    val songId: Long
)

package com.example.liliplayer.util

import java.util.Locale
import java.util.concurrent.TimeUnit

fun Long.formatDuration(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, seconds)
    }
}

fun Int.formatSongCount(): String {
    return when (this) {
        1 -> "1 song"
        else -> "$this songs"
    }
}

fun Int.formatAlbumCount(): String {
    return when (this) {
        1 -> "1 album"
        else -> "$this albums"
    }
}

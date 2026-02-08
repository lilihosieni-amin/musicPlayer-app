package com.example.liliplayer.util

object Constants {
    const val MIN_SONG_DURATION_MS = 30_000L
    const val POSITION_UPDATE_INTERVAL_MS = 250L
    const val SEARCH_DEBOUNCE_MS = 300L
    val SLEEP_TIMER_OPTIONS = listOf(
        15 * 60 * 1000L to "15 minutes",
        30 * 60 * 1000L to "30 minutes",
        45 * 60 * 1000L to "45 minutes",
        60 * 60 * 1000L to "1 hour",
        90 * 60 * 1000L to "1.5 hours",
        120 * 60 * 1000L to "2 hours"
    )
}

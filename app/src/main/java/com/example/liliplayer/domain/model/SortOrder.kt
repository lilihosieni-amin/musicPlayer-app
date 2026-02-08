package com.example.liliplayer.domain.model

enum class SortOrder(val displayName: String) {
    TITLE_ASC("Title A-Z"),
    TITLE_DESC("Title Z-A"),
    ARTIST_ASC("Artist A-Z"),
    ARTIST_DESC("Artist Z-A"),
    ALBUM_ASC("Album A-Z"),
    ALBUM_DESC("Album Z-A"),
    YEAR_ASC("Year (Oldest)"),
    YEAR_DESC("Year (Newest)"),
    DURATION_ASC("Duration (Short)"),
    DURATION_DESC("Duration (Long)"),
    DATE_ADDED_ASC("Date Added (Oldest)"),
    DATE_ADDED_DESC("Date Added (Newest)")
}

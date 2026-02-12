package com.example.liliplayer.ui.navigation

sealed class Screen(val route: String) {
    data object Songs : Screen("songs")
    data object Albums : Screen("albums")
    data object Artists : Screen("artists")
    data object Playlists : Screen("playlists")
    data object Genres : Screen("genres")
    data object Tags : Screen("tags")
    data object AlbumDetail : Screen("album/{albumId}") {
        fun createRoute(albumId: Long) = "album/$albumId"
    }
    data object ArtistDetail : Screen("artist/{artistId}") {
        fun createRoute(artistId: Long) = "artist/$artistId"
    }
    data object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist/$playlistId"
    }
    data object GenreDetail : Screen("genre/{genreId}") {
        fun createRoute(genreId: Long) = "genre/$genreId"
    }
    data object TagDetail : Screen("tag/{tagId}") {
        fun createRoute(tagId: Long) = "tag/$tagId"
    }
    data object SmartPlaylistDetail : Screen("smart_playlist/{type}") {
        fun createRoute(type: String) = "smart_playlist/$type"
    }
    data object Favorites : Screen("favorites")
    data object NowPlaying : Screen("now_playing")
    data object Queue : Screen("queue")
    data object Search : Screen("search")
    data object Equalizer : Screen("equalizer")
    data object Settings : Screen("settings")
    data object BeelodyDownloader : Screen("beelody_downloader")
}

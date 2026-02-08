package com.example.liliplayer.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.liliplayer.domain.model.Song
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.components.MetadataEditorDialog
import com.example.liliplayer.ui.components.PlaylistPickerDialog
import com.example.liliplayer.ui.components.TagPickerDialog
import com.example.liliplayer.ui.screens.albums.AlbumDetailScreen
import com.example.liliplayer.ui.screens.albums.AlbumsScreen
import com.example.liliplayer.ui.screens.artists.ArtistDetailScreen
import com.example.liliplayer.ui.screens.artists.ArtistsScreen
import com.example.liliplayer.ui.screens.equalizer.EqualizerScreen
import com.example.liliplayer.ui.screens.genres.GenreDetailScreen
import com.example.liliplayer.ui.screens.genres.GenresScreen
import com.example.liliplayer.ui.screens.nowplaying.NowPlayingScreen
import com.example.liliplayer.ui.screens.nowplaying.NowPlayingViewModel
import com.example.liliplayer.ui.screens.playlists.PlaylistDetailScreen
import com.example.liliplayer.ui.screens.playlists.PlaylistsScreen
import com.example.liliplayer.ui.screens.playlists.SmartPlaylistDetailScreen
import com.example.liliplayer.ui.screens.queue.QueueScreen
import com.example.liliplayer.ui.screens.search.SearchScreen
import com.example.liliplayer.ui.screens.settings.SettingsScreen
import com.example.liliplayer.ui.screens.shared.SongActionsViewModel
import com.example.liliplayer.ui.screens.songs.SongsScreen
import com.example.liliplayer.ui.screens.tags.TagDetailScreen
import com.example.liliplayer.ui.screens.tags.TagsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    playbackController: PlaybackController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Songs.route
    ) {
        composable(Screen.Songs.route) {
            SongsScreen(
                playbackController = playbackController,
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(Screen.Albums.route) {
            AlbumsScreen(
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                }
            )
        }

        composable(Screen.Artists.route) {
            ArtistsScreen(
                onArtistClick = { artistId ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                }
            )
        }

        composable(Screen.Playlists.route) {
            PlaylistsScreen(
                onPlaylistClick = { playlistId ->
                    navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                },
                onSmartPlaylistClick = { type ->
                    navController.navigate(Screen.SmartPlaylistDetail.createRoute(type))
                }
            )
        }

        composable(Screen.Genres.route) {
            GenresScreen(
                onGenreClick = { genreId ->
                    navController.navigate(Screen.GenreDetail.createRoute(genreId))
                }
            )
        }

        composable(Screen.Tags.route) {
            TagsScreen(
                onTagClick = { tagId ->
                    navController.navigate(Screen.TagDetail.createRoute(tagId))
                }
            )
        }

        composable(
            route = Screen.AlbumDetail.route,
            arguments = listOf(navArgument("albumId") { type = NavType.LongType })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getLong("albumId") ?: return@composable
            AlbumDetailScreen(
                albumId = albumId,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getLong("artistId") ?: return@composable
            ArtistDetailScreen(
                artistId = artistId,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onAlbumClick = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(
            route = Screen.PlaylistDetail.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: return@composable
            PlaylistDetailScreen(
                playlistId = playlistId,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(
            route = Screen.GenreDetail.route,
            arguments = listOf(navArgument("genreId") { type = NavType.LongType })
        ) { backStackEntry ->
            val genreId = backStackEntry.arguments?.getLong("genreId") ?: return@composable
            GenreDetailScreen(
                genreId = genreId,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(
            route = Screen.TagDetail.route,
            arguments = listOf(navArgument("tagId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tagId = backStackEntry.arguments?.getLong("tagId") ?: return@composable
            TagDetailScreen(
                tagId = tagId,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(
            route = Screen.SmartPlaylistDetail.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: return@composable
            SmartPlaylistDetailScreen(
                type = type,
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(Screen.Favorites.route) {
            SmartPlaylistDetailScreen(
                type = "favorites",
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToNowPlaying = { navController.navigate(Screen.NowPlaying.route) }
            )
        }

        composable(Screen.NowPlaying.route) {
            val nowPlayingVm: NowPlayingViewModel = hiltViewModel()
            val songActionsVm: SongActionsViewModel = hiltViewModel()
            val playbackState by playbackController.playbackState.collectAsState()
            val isFavorite by nowPlayingVm.isFavorite.collectAsState()
            val songTagIds by nowPlayingVm.songTagIds.collectAsState()
            val lyrics by nowPlayingVm.lyrics.collectAsState()
            val lyricsLoading by nowPlayingVm.lyricsLoading.collectAsState()

            var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
            var songToTag by remember { mutableStateOf<Song?>(null) }
            var songToEdit by remember { mutableStateOf<Song?>(null) }

            LaunchedEffect(playbackState.currentSong?.id) {
                nowPlayingVm.setSongId(playbackState.currentSong?.id)
            }

            NowPlayingScreen(
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToQueue = { navController.navigate(Screen.Queue.route) },
                isFavorite = isFavorite,
                onToggleFavorite = { songId -> nowPlayingVm.toggleFavorite(songId) },
                onAddToPlaylist = { song -> songToAddToPlaylist = song },
                onManageTags = { song -> songToTag = song },
                onEditMetadata = { song -> songToEdit = song },
                lyrics = lyrics,
                lyricsLoading = lyricsLoading,
                onLoadLyrics = { song ->
                    nowPlayingVm.loadLyrics(song.artist, song.title, song.contentUri)
                },
                onClearLyrics = { nowPlayingVm.clearLyrics() }
            )

            songToAddToPlaylist?.let { song ->
                PlaylistPickerDialog(
                    onDismiss = { songToAddToPlaylist = null },
                    onPlaylistSelected = { playlistId ->
                        songActionsVm.addSongToPlaylist(song.id, playlistId)
                        songToAddToPlaylist = null
                    },
                    onCreatePlaylist = { name ->
                        songActionsVm.createPlaylistAndAddSong(name, song.id)
                        songToAddToPlaylist = null
                    }
                )
            }

            songToTag?.let { song ->
                TagPickerDialog(
                    songId = song.id,
                    songTagIds = songTagIds,
                    onDismiss = { songToTag = null },
                    onToggleTag = { tagId, add ->
                        nowPlayingVm.toggleTagForSong(tagId, song.id, add)
                    }
                )
            }

            songToEdit?.let { song ->
                MetadataEditorDialog(
                    song = song,
                    onDismiss = { songToEdit = null },
                    onSave = { title, artist, album ->
                        songActionsVm.updateSongMetadata(song.id, title, artist, album)
                        songToEdit = null
                    }
                )
            }
        }

        composable(Screen.Queue.route) {
            QueueScreen(
                playbackController = playbackController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                playbackController = playbackController,
                onBack = { navController.popBackStack() },
                onNavigateToAlbum = { albumId ->
                    navController.navigate(Screen.AlbumDetail.createRoute(albumId))
                },
                onNavigateToArtist = { artistId ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                }
            )
        }

        composable(Screen.Equalizer.route) {
            EqualizerScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}

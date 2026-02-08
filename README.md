# Lili Player

A modern Android music player built with Jetpack Compose and Material 3, featuring a **Colorful Minimalist** UI design.

## Features

- **Music Library** - Browse songs, albums, artists, and genres from your device
- **Now Playing** - Spotify-style player with circular album art, gradient controls, and thick seek bar
- **Playlists** - Create, rename, and manage custom playlists
- **Smart Playlists** - Auto-generated lists: Favorites, Most Played, Recently Played, Recently Added
- **Favorites** - Heart songs from any screen
- **Tags** - Create custom tags and assign them to songs for flexible organization
- **Queue Management** - View and reorder the playback queue
- **Search** - Search across songs, albums, and artists
- **Lyrics** - View embedded lyrics or fetch from LRCLIB online
- **Metadata Editor** - Edit song title, artist, and album info
- **Equalizer** - 5-band equalizer with preset support
- **Background Playback** - Media3-powered playback service with notification controls

## UI Design

The app uses a **Colorful Minimalist** aesthetic:

- Clean white backgrounds
- Vibrant accent colors: Mint Green, Soft Pink, Sky Blue, Lavender, Sunny Yellow
- Colorful outer glow shadows on cards (no harsh borders)
- Gradient play/pause button and progress bar
- Large rounded corners (20-32dp) throughout
- Navigation drawer with colorful accent items

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM with Hilt dependency injection
- **Playback:** AndroidX Media3 (ExoPlayer)
- **Database:** Room (playlists, favorites, tags, play counts, recents)
- **Image Loading:** Coil
- **Navigation:** Navigation Compose
- **Permissions:** Accompanist Permissions

## Build

- **AGP:** 9.0.0
- **Kotlin:** 2.2.10 (embedded in AGP)
- **Compile SDK:** 36
- **Min SDK:** 26

```bash
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Permissions

- `READ_MEDIA_AUDIO` - Access music files
- `MANAGE_EXTERNAL_STORAGE` - Delete and edit music files
- `FOREGROUND_SERVICE` - Background playback
- `POST_NOTIFICATIONS` - Playback notification controls

## License

This project is for personal use.

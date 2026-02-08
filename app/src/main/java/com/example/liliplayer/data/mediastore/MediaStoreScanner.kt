package com.example.liliplayer.data.mediastore

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.liliplayer.domain.model.Album
import com.example.liliplayer.domain.model.Artist
import com.example.liliplayer.domain.model.Genre
import com.example.liliplayer.domain.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class DeleteResult {
    data object Success : DeleteResult()
    data class RequiresPermission(val intentSender: IntentSender) : DeleteResult()
    data class Error(val message: String) : DeleteResult()
}

@Singleton
class MediaStoreScanner @Inject constructor(
    private val contentResolver: ContentResolver
) {
    companion object {
        private const val MIN_DURATION_MS = 30_000L
        private val ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")
    }

    suspend fun querySongs(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(MIN_DURATION_MS.toString())
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(collection, projection, selection, selectionArgs, sortOrder)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val albumId = cursor.getLong(albumIdCol)
                songs.add(
                    Song(
                        id = id,
                        title = cursor.getString(titleCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown",
                        artistId = cursor.getLong(artistIdCol),
                        album = cursor.getString(albumCol) ?: "Unknown",
                        albumId = albumId,
                        duration = cursor.getLong(durationCol),
                        trackNumber = cursor.getInt(trackCol),
                        year = cursor.getInt(yearCol),
                        dateAdded = cursor.getLong(dateAddedCol),
                        contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                        albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, albumId)
                    )
                )
            }
        }
        songs
    }

    suspend fun queryAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
            MediaStore.Audio.Albums.FIRST_YEAR
        )

        contentResolver.query(collection, projection, null, null, "${MediaStore.Audio.Albums.ALBUM} ASC")?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val songCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                albums.add(
                    Album(
                        id = id,
                        name = cursor.getString(albumCol) ?: "Unknown",
                        artist = cursor.getString(artistCol) ?: "Unknown",
                        artistId = 0,
                        songCount = cursor.getInt(songCountCol),
                        year = cursor.getInt(yearCol),
                        albumArtUri = ContentUris.withAppendedId(ALBUM_ART_URI, id)
                    )
                )
            }
        }
        albums
    }

    suspend fun queryArtists(): List<Artist> = withContext(Dispatchers.IO) {
        val artists = mutableListOf<Artist>()
        val collection = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        contentResolver.query(collection, projection, null, null, "${MediaStore.Audio.Artists.ARTIST} ASC")?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val albumCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val songCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)

            while (cursor.moveToNext()) {
                artists.add(
                    Artist(
                        id = cursor.getLong(idCol),
                        name = cursor.getString(artistCol) ?: "Unknown",
                        albumCount = cursor.getInt(albumCountCol),
                        songCount = cursor.getInt(songCountCol)
                    )
                )
            }
        }
        artists
    }

    suspend fun queryGenres(): List<Genre> = withContext(Dispatchers.IO) {
        val genres = mutableListOf<Genre>()
        val collection = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME
        )

        contentResolver.query(collection, projection, null, null, "${MediaStore.Audio.Genres.NAME} ASC")?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val memberCursor = contentResolver.query(
                    MediaStore.Audio.Genres.Members.getContentUri("external", id),
                    arrayOf(MediaStore.Audio.Genres.Members._ID),
                    null, null, null
                )
                val count = memberCursor?.count ?: 0
                memberCursor?.close()

                genres.add(
                    Genre(
                        id = id,
                        name = cursor.getString(nameCol) ?: "Unknown",
                        songCount = count
                    )
                )
            }
        }
        genres
    }

    suspend fun querySongsForGenre(genreId: Long): List<Long> = withContext(Dispatchers.IO) {
        val songIds = mutableListOf<Long>()
        val uri = MediaStore.Audio.Genres.Members.getContentUri("external", genreId)
        contentResolver.query(
            uri,
            arrayOf(MediaStore.Audio.Genres.Members.AUDIO_ID),
            null, null, null
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.Members.AUDIO_ID)
            while (cursor.moveToNext()) {
                songIds.add(cursor.getLong(idCol))
            }
        }
        songIds
    }

    suspend fun updateSongMetadata(
        songId: Long,
        title: String,
        artist: String,
        album: String
    ): UpdateResult = withContext(Dispatchers.IO) {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        val values = android.content.ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, title)
            put(MediaStore.Audio.Media.ARTIST, artist)
            put(MediaStore.Audio.Media.ALBUM, album)
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && android.os.Environment.isExternalStorageManager()) {
                // Has MANAGE_EXTERNAL_STORAGE - can update directly
                val rows = contentResolver.update(uri, values, null, null)
                if (rows > 0) UpdateResult.Success else UpdateResult.Error("Failed to update")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent = MediaStore.createWriteRequest(contentResolver, listOf(uri))
                UpdateResult.RequiresPermission(pendingIntent.intentSender)
            } else {
                val rows = contentResolver.update(uri, values, null, null)
                if (rows > 0) UpdateResult.Success else UpdateResult.Error("Failed to update")
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                UpdateResult.RequiresPermission(e.userAction.actionIntent.intentSender)
            } else {
                UpdateResult.Error(e.message ?: "Permission denied")
            }
        }
    }

    suspend fun deleteSong(songId: Long): DeleteResult = withContext(Dispatchers.IO) {
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && android.os.Environment.isExternalStorageManager()) {
                // Has MANAGE_EXTERNAL_STORAGE - can delete directly
                val rows = contentResolver.delete(uri, null, null)
                if (rows > 0) DeleteResult.Success else DeleteResult.Error("Failed to delete")
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val pendingIntent = MediaStore.createDeleteRequest(contentResolver, listOf(uri))
                DeleteResult.RequiresPermission(pendingIntent.intentSender)
            } else {
                val rows = contentResolver.delete(uri, null, null)
                if (rows > 0) DeleteResult.Success else DeleteResult.Error("Failed to delete")
            }
        } catch (e: SecurityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                DeleteResult.RequiresPermission(e.userAction.actionIntent.intentSender)
            } else {
                DeleteResult.Error(e.message ?: "Permission denied")
            }
        }
    }
}

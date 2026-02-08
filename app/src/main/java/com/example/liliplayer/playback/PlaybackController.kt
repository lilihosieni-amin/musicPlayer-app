package com.example.liliplayer.playback

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.liliplayer.data.local.dao.PlayCountDao
import com.example.liliplayer.data.local.dao.RecentDao
import com.example.liliplayer.data.local.entity.PlayCountEntity
import com.example.liliplayer.data.local.entity.RecentEntity
import com.example.liliplayer.domain.model.PlaybackState
import com.example.liliplayer.domain.model.RepeatMode
import com.example.liliplayer.domain.model.Song
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sessionToken: SessionToken,
    private val recentDao: RecentDao,
    private val playCountDao: PlayCountDao
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var currentQueue: List<Song> = emptyList()
    private var positionJob: Job? = null

    fun connect() {
        if (controllerFuture != null) return
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture = future
        future.addListener({
            controller = future.get()
            controller?.addListener(playerListener)
            startPositionUpdates()
        }, MoreExecutors.directExecutor())
    }

    fun disconnect() {
        positionJob?.cancel()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        controller = null
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            updateState()
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            updateState()
            // Record play count and recent play
            val currentIndex = controller?.currentMediaItemIndex ?: -1
            if (currentIndex in currentQueue.indices) {
                val song = currentQueue[currentIndex]
                scope.launch(Dispatchers.IO) {
                    recentDao.addRecent(RecentEntity(songId = song.id))
                    val existing = playCountDao.getPlayCount(song.id)
                    val newCount = (existing?.count ?: 0) + 1
                    playCountDao.upsertPlayCount(PlayCountEntity(songId = song.id, count = newCount))
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            updateState()
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            updateState()
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            updateState()
        }
    }

    private fun startPositionUpdates() {
        positionJob?.cancel()
        positionJob = scope.launch {
            while (isActive) {
                updateState()
                delay(250)
            }
        }
    }

    private fun updateState() {
        val player = controller ?: return
        val currentIndex = player.currentMediaItemIndex
        val currentSong = if (currentIndex in currentQueue.indices) currentQueue[currentIndex] else null

        _playbackState.value = PlaybackState(
            currentSong = currentSong,
            isPlaying = player.isPlaying,
            position = player.currentPosition.coerceAtLeast(0),
            duration = player.duration.coerceAtLeast(0),
            shuffleEnabled = player.shuffleModeEnabled,
            repeatMode = when (player.repeatMode) {
                Player.REPEAT_MODE_ONE -> RepeatMode.ONE
                Player.REPEAT_MODE_ALL -> RepeatMode.ALL
                else -> RepeatMode.OFF
            },
            queue = currentQueue,
            currentIndex = currentIndex
        )
    }

    fun playSongs(songs: List<Song>, startIndex: Int = 0) {
        val player = controller ?: return
        currentQueue = songs
        val mediaItems = songs.map { it.toMediaItem() }
        player.setMediaItems(mediaItems, startIndex, 0)
        player.prepare()
        player.play()
    }

    fun play() { controller?.play() }
    fun pause() { controller?.pause() }
    fun playPause() {
        val player = controller ?: return
        if (player.isPlaying) player.pause() else player.play()
    }

    fun next() { controller?.seekToNextMediaItem() }
    fun previous() { controller?.seekToPreviousMediaItem() }

    fun seekTo(position: Long) { controller?.seekTo(position) }

    fun toggleShuffle() {
        val player = controller ?: return
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }

    fun toggleRepeat() {
        val player = controller ?: return
        player.repeatMode = when (player.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            Player.REPEAT_MODE_ALL -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
    }

    fun addToQueue(song: Song) {
        val player = controller ?: return
        currentQueue = currentQueue + song
        player.addMediaItem(song.toMediaItem())
    }

    fun removeFromQueue(index: Int) {
        val player = controller ?: return
        if (index in currentQueue.indices) {
            currentQueue = currentQueue.toMutableList().apply { removeAt(index) }
            player.removeMediaItem(index)
        }
    }

    fun moveQueueItem(from: Int, to: Int) {
        val player = controller ?: return
        if (from in currentQueue.indices && to in currentQueue.indices) {
            currentQueue = currentQueue.toMutableList().apply {
                add(to, removeAt(from))
            }
            player.moveMediaItem(from, to)
        }
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(contentUri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(albumArtUri)
                    .build()
            )
            .build()
    }
}

package com.example.liliplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.liliplayer.playback.PlaybackController
import com.example.liliplayer.ui.screens.HomeScreen
import com.example.liliplayer.ui.theme.LiliPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var playbackController: PlaybackController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        playbackController.connect()
        setContent {
            LiliPlayerTheme {
                HomeScreen(playbackController = playbackController)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playbackController.disconnect()
    }
}

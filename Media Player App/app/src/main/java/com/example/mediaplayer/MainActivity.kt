package com.example.mediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import com.example.mediaplayer.audio.AudioPlayerController
import com.example.mediaplayer.audio.AudioUiState
import com.example.mediaplayer.ui.MediaPlayerScreen
import com.example.mediaplayer.ui.theme.MediaPlayerTheme

class MainActivity : ComponentActivity() {

    private val audioUiState = mutableStateOf(AudioUiState())

    private lateinit var audioController: AudioPlayerController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioController = AudioPlayerController(this) { audioUiState.value = it }
        enableEdgeToEdge()
        setContent {
            val pickAudioLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                if (uri != null) {
                    audioController.loadAudio(uri)
                }
            }

            val audio by audioUiState
            MediaPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MediaPlayerScreen(
                        audioUiState = audio,
                        onOpenAudioFile = { pickAudioLauncher.launch("audio/*") },
                        onAudioPlay = audioController::play,
                        onAudioPause = audioController::pause,
                        onAudioStop = audioController::stop,
                        onAudioRestart = audioController::restart
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        audioController.release()
        super.onDestroy()
    }
}

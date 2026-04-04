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
import com.example.mediaplayer.playback.PlaybackCoordinator
import com.example.mediaplayer.ui.MediaPlayerScreen
import com.example.mediaplayer.ui.theme.MediaPlayerTheme
import com.example.mediaplayer.video.VideoPlaybackController
import com.example.mediaplayer.video.VideoUiState

class MainActivity : ComponentActivity() {

    private val audioUiState = mutableStateOf(AudioUiState())
    private val videoUiState = mutableStateOf(VideoUiState())

    private lateinit var audioController: AudioPlayerController
    private lateinit var videoController: VideoPlaybackController
    private lateinit var playbackCoordinator: PlaybackCoordinator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioController = AudioPlayerController(this) { audioUiState.value = it }
        videoController = VideoPlaybackController(this) { videoUiState.value = it }
        playbackCoordinator = PlaybackCoordinator(
            audio = audioController,
            video = videoController,
            currentAudioState = { audioUiState.value },
            currentVideoState = { videoUiState.value }
        )
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
            val video by videoUiState
            MediaPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MediaPlayerScreen(
                        videoController = videoController,
                        audioUiState = audio,
                        videoUiState = video,
                        onOpenAudioFile = { pickAudioLauncher.launch("audio/*") },
                        onAudioPlay = { playAudio() },
                        onAudioPause = { pauseAudio() },
                        onAudioStop = { stopAudio() },
                        onAudioRestart = { restartAudio() },
                        onOpenVideoUrl = { videoController.openUrl() },
                        onVideoUrlChange = videoController::onUrlChange,
                        onVideoPlay = { playVideo() },
                        onVideoPause = { pauseVideo() },
                        onVideoStop = { stopVideo() },
                        onVideoRestart = { restartVideo() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        audioController.release()
        super.onDestroy()
    }

    private fun playAudio() = playbackCoordinator.playAudio()

    private fun pauseAudio() = playbackCoordinator.pauseAudio()

    private fun stopAudio() = playbackCoordinator.stopAudio()

    private fun restartAudio() = playbackCoordinator.restartAudio()

    private fun playVideo() = playbackCoordinator.playVideo()

    private fun pauseVideo() = playbackCoordinator.pauseVideo()

    private fun stopVideo() = playbackCoordinator.stopVideo()

    private fun restartVideo() = playbackCoordinator.restartVideo()
}

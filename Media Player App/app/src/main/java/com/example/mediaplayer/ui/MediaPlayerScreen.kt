package com.example.mediaplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.VideoView
import com.example.mediaplayer.audio.AudioUiState
import com.example.mediaplayer.ui.theme.MediaPlayerTheme
import com.example.mediaplayer.video.VideoPlaybackController
import com.example.mediaplayer.video.VideoUiState

private data class PlaybackButton(
    val label: String,
    val onClick: () -> Unit,
    val enabled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerScreen(
    videoController: VideoPlaybackController,
    audioUiState: AudioUiState = AudioUiState(),
    videoUiState: VideoUiState = VideoUiState(),
    onOpenAudioFile: () -> Unit = {},
    onAudioPlay: () -> Unit = {},
    onAudioPause: () -> Unit = {},
    onAudioStop: () -> Unit = {},
    onAudioRestart: () -> Unit = {},
    onOpenVideoUrl: () -> Unit = {},
    onVideoUrlChange: (String) -> Unit = {},
    onVideoPlay: () -> Unit = {},
    onVideoPause: () -> Unit = {},
    onVideoStop: () -> Unit = {},
    onVideoRestart: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Media Player") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AudioPlayerCard(
                audioUiState = audioUiState,
                onOpenAudioFile = onOpenAudioFile,
                onPlay = onAudioPlay,
                onPause = onAudioPause,
                onStop = onAudioStop,
                onRestart = onAudioRestart
            )
            VideoPlayerCard(
                videoUiState = videoUiState,
                videoController = videoController,
                onOpenVideoUrl = onOpenVideoUrl,
                onVideoUrlChange = onVideoUrlChange,
                onVideoPlay = onVideoPlay,
                onVideoPause = onVideoPause,
                onVideoStop = onVideoStop,
                onVideoRestart = onVideoRestart
            )
        }
    }
}

@Composable
private fun AudioPlayerCard(
    audioUiState: AudioUiState,
    onOpenAudioFile: () -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onRestart: () -> Unit
) {
    val playEnabled = audioUiState.isAudioLoaded && !audioUiState.isAudioPlaying
    val pauseEnabled = audioUiState.isAudioPlaying
    val stopEnabled = audioUiState.isAudioInitialized
    val restartEnabled = audioUiState.isAudioLoaded

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Audio Player",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            ControlButtonRow(
                buttons = listOf(
                    PlaybackButton("Open Audio File", onOpenAudioFile, enabled = true),
                    PlaybackButton("Play", onPlay, enabled = playEnabled),
                    PlaybackButton("Pause", onPause, enabled = pauseEnabled)
                )
            )
            ControlButtonRow(
                buttons = listOf(
                    PlaybackButton("Stop", onStop, enabled = stopEnabled),
                    PlaybackButton("Restart", onRestart, enabled = restartEnabled)
                )
            )
            Text(
                text = "Status: ${audioUiState.audioStatus}",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun VideoPlayerCard(
    videoUiState: VideoUiState,
    videoController: VideoPlaybackController,
    onOpenVideoUrl: () -> Unit,
    onVideoUrlChange: (String) -> Unit,
    onVideoPlay: () -> Unit,
    onVideoPause: () -> Unit,
    onVideoStop: () -> Unit,
    onVideoRestart: () -> Unit
) {
    val playEnabled =
        videoUiState.isVideoBound && videoUiState.isVideoLoaded && !videoUiState.isVideoPlaying
    val pauseEnabled = videoUiState.isVideoPlaying
    val stopEnabled =
        videoUiState.isVideoBound && videoUiState.videoUri != null
    val restartEnabled =
        videoUiState.isVideoBound && videoUiState.isVideoLoaded

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Video Player",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = videoUiState.videoUrl,
                onValueChange = onVideoUrlChange,
                label = { Text("Enter Video URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ControlButtonRow(
                buttons = listOf(
                    PlaybackButton("Open URL", onOpenVideoUrl, enabled = true),
                    PlaybackButton("Play", onVideoPlay, enabled = playEnabled),
                    PlaybackButton("Pause", onVideoPause, enabled = pauseEnabled)
                )
            )
            ControlButtonRow(
                buttons = listOf(
                    PlaybackButton("Stop", onVideoStop, enabled = stopEnabled),
                    PlaybackButton("Restart", onVideoRestart, enabled = restartEnabled)
                )
            )
            AndroidView(
                factory = { context ->
                    VideoView(context).also { videoView ->
                        videoController.bindView(videoView)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                onRelease = { videoView ->
                    try {
                        videoView.stopPlayback()
                    } catch (_: Exception) {
                        // ignore
                    }
                    videoController.unbindView()
                }
            )
            Text(
                text = "Status: ${videoUiState.videoStatus}",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ControlButtonRow(
    buttons: List<PlaybackButton>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttons.forEach { button ->
            Button(
                onClick = button.onClick,
                enabled = button.enabled,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = button.label,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPlayerScreenPreview() {
    val videoState = remember { mutableStateOf(VideoUiState()) }
    val videoController = remember {
        VideoPlaybackController { videoState.value = it }
    }
    val videoUi by videoState
    MediaPlayerTheme {
        MediaPlayerScreen(
            videoController = videoController,
            videoUiState = videoUi
        )
    }
}

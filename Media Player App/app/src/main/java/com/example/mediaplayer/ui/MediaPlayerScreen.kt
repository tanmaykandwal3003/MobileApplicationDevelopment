package com.example.mediaplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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

@Composable
private fun StatusLine(status: String) {
    val color = when (status) {
        "Playing" -> Color(0xFF2E7D32)
        "Paused" -> Color(0xFFF57C00)
        "Stopped" -> Color(0xFFC62828)
        else -> Color(0xFF757575)
    }
    Text(
        text = "Status: $status",
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        color = color
    )
}

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
                .padding(horizontal = 16.dp, vertical = 16.dp)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Audio Player",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (!audioUiState.audioDisplayName.isNullOrBlank()) {
                Text(
                    text = "File: ${audioUiState.audioDisplayName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
            StatusLine(status = audioUiState.audioStatus)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
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
            if (videoUiState.videoUrl.isNotBlank()) {
                Text(
                    text = "URL: ${videoUiState.videoUrlDisplay}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                AndroidView(
                    factory = { context ->
                        VideoView(context).also { videoView ->
                            videoController.bindView(videoView)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    onRelease = { videoView ->
                        try {
                            videoView.stopPlayback()
                        } catch (_: Exception) {
                            // ignore
                        }
                        videoController.unbindView()
                    }
                )
            }
            StatusLine(status = videoUiState.videoStatus)
        }
    }
}

@Composable
private fun ControlButtonRow(
    buttons: List<PlaybackButton>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        buttons.forEach { button ->
            Button(
                onClick = button.onClick,
                enabled = button.enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = button.label,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaPlayerScreenPreview() {
    val context = LocalContext.current
    val videoState = remember { mutableStateOf(VideoUiState()) }
    val videoController = remember {
        VideoPlaybackController(context) { videoState.value = it }
    }
    val videoUi by videoState
    MediaPlayerTheme {
        MediaPlayerScreen(
            videoController = videoController,
            videoUiState = videoUi
        )
    }
}

package com.example.mediaplayer.ui

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediaplayer.audio.AudioUiState
import com.example.mediaplayer.ui.theme.MediaPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerScreen(
    audioUiState: AudioUiState = AudioUiState(),
    onOpenAudioFile: () -> Unit = {},
    onAudioPlay: () -> Unit = {},
    onAudioPause: () -> Unit = {},
    onAudioStop: () -> Unit = {},
    onAudioRestart: () -> Unit = {}
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
            VideoPlayerCard()
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
                    "Open Audio File" to onOpenAudioFile,
                    "Play" to onPlay,
                    "Pause" to onPause
                )
            )
            ControlButtonRow(
                buttons = listOf(
                    "Stop" to onStop,
                    "Restart" to onRestart
                )
            )
            Text(
                text = "Status: ${audioUiState.status}",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun VideoPlayerCard() {
    var videoUrl by remember { mutableStateOf("") }

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
                value = videoUrl,
                onValueChange = { videoUrl = it },
                label = { Text("Enter Video URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            ControlButtonRow(
                buttons = listOf(
                    "Open URL" to {},
                    "Play" to {},
                    "Pause" to {}
                )
            )
            ControlButtonRow(
                buttons = listOf(
                    "Stop" to {},
                    "Restart" to {}
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("Video Player Area")
            }
            Text(
                text = "Status: Idle",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ControlButtonRow(
    buttons: List<Pair<String, () -> Unit>>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        buttons.forEach { (label, onClick) ->
            Button(
                onClick = onClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
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
    MediaPlayerTheme {
        MediaPlayerScreen()
    }
}

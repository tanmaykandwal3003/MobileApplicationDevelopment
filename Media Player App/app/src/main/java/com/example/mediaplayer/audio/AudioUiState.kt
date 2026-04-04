package com.example.mediaplayer.audio

import android.net.Uri

data class AudioUiState(
    val selectedAudioUri: Uri? = null,
    val isPlaying: Boolean = false,
    val status: String = "No file selected"
)

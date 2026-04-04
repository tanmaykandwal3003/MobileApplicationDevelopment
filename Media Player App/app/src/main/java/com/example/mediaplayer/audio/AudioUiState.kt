package com.example.mediaplayer.audio

import android.net.Uri

data class AudioUiState(
    val selectedAudioUri: Uri? = null,
    val audioStatus: String = "No file selected",
    val isAudioPlaying: Boolean = false,
    val isAudioLoaded: Boolean = false,
    val isAudioInitialized: Boolean = false
)

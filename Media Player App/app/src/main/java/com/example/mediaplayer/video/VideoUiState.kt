package com.example.mediaplayer.video

import android.net.Uri

data class VideoUiState(
    val videoUrl: String = "",
    val videoUri: Uri? = null,
    val videoUrlDisplay: String = "",
    val videoStatus: String = "No file selected",
    val isVideoPlaying: Boolean = false,
    val isVideoLoaded: Boolean = false,
    val isVideoBound: Boolean = false
)

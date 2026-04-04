package com.example.mediaplayer.video

import android.net.Uri

data class VideoUiState(
    val videoUrl: String = "",
    val videoUri: Uri? = null,
    val videoStatus: String = "No URL entered"
)

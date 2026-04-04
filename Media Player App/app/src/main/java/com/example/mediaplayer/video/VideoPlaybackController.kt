package com.example.mediaplayer.video

import android.net.Uri
import android.webkit.URLUtil
import android.widget.VideoView

class VideoPlaybackController(
    private val onState: (VideoUiState) -> Unit
) {

    private var videoView: VideoView? = null
    private var videoUrl: String = ""
    private var videoUri: Uri? = null
    private var videoStatus: String = "No URL entered"

    init {
        emit()
    }

    private fun emit() {
        onState(
            VideoUiState(
                videoUrl = videoUrl,
                videoUri = videoUri,
                videoStatus = videoStatus
            )
        )
    }

    fun bindView(view: VideoView) {
        videoView = view
        view.setOnErrorListener { _, _, _ ->
            videoStatus = "Stopped"
            emit()
            true
        }
        view.setOnCompletionListener {
            videoStatus = "Stopped"
            emit()
        }
        videoUri?.let { uri ->
            applyVideoUri(view, uri)
        }
    }

    fun unbindView() {
        videoView?.stopPlayback()
        videoView = null
    }

    fun onUrlChange(text: String) {
        videoUrl = text
        emit()
    }

    fun openUrl() {
        val trimmed = videoUrl.trim()
        if (trimmed.isEmpty()) {
            videoStatus = "No URL entered"
            emit()
            return
        }
        if (!URLUtil.isValidUrl(trimmed)) {
            videoStatus = "Stopped"
            emit()
            return
        }
        val uri = try {
            Uri.parse(trimmed)
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
            return
        }
        if (uri.scheme.isNullOrBlank() || uri.host.isNullOrBlank()) {
            videoStatus = "Stopped"
            emit()
            return
        }
        videoUri = uri
        val vv = videoView
        if (vv != null) {
            applyVideoUri(vv, uri)
        } else {
            videoStatus = "Stopped"
            emit()
        }
    }

    private fun applyVideoUri(vv: VideoView, uri: Uri) {
        try {
            vv.setOnPreparedListener {
                videoStatus = "Video loaded"
                emit()
            }
            vv.setVideoURI(uri)
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
        }
    }

    fun play() {
        if (videoUri == null) {
            videoStatus = "No URL entered"
            emit()
            return
        }
        val vv = videoView ?: run {
            videoStatus = "Stopped"
            emit()
            return
        }
        try {
            vv.start()
            videoStatus = "Playing"
            emit()
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
        }
    }

    fun pause() {
        val vv = videoView ?: return
        try {
            vv.pause()
            videoStatus = "Paused"
            emit()
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
        }
    }

    fun stop() {
        val uri = videoUri ?: run {
            videoStatus = "No URL entered"
            emit()
            return
        }
        val vv = videoView ?: run {
            videoStatus = "Stopped"
            emit()
            return
        }
        try {
            vv.stopPlayback()
            vv.setOnPreparedListener {
                videoStatus = "Stopped"
                emit()
            }
            vv.setVideoURI(uri)
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
        }
    }

    fun restart() {
        if (videoUri == null) {
            videoStatus = "No URL entered"
            emit()
            return
        }
        val vv = videoView ?: run {
            videoStatus = "Stopped"
            emit()
            return
        }
        try {
            @Suppress("DEPRECATION")
            vv.seekTo(0)
            vv.start()
            videoStatus = "Playing"
            emit()
        } catch (_: Exception) {
            videoStatus = "Stopped"
            emit()
        }
    }
}

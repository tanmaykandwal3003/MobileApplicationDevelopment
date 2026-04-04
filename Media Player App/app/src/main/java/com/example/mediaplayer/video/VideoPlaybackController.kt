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
    private var videoStatus: String = "No file selected"
    private var isVideoPlaying: Boolean = false
    private var isVideoLoaded: Boolean = false

    init {
        emit()
    }

    private fun emit() {
        onState(
            VideoUiState(
                videoUrl = videoUrl,
                videoUri = videoUri,
                videoStatus = videoStatus,
                isVideoPlaying = isVideoPlaying,
                isVideoLoaded = isVideoLoaded,
                isVideoBound = videoView != null
            )
        )
    }

    fun bindView(view: VideoView) {
        videoView = view
        view.setOnErrorListener { _, _, _ ->
            isVideoPlaying = false
            isVideoLoaded = false
            videoStatus = "Stopped"
            emit()
            true
        }
        view.setOnCompletionListener {
            isVideoPlaying = false
            if (videoUri != null) {
                videoStatus = "Ready"
            } else {
                videoStatus = "No file selected"
            }
            emit()
        }
        videoUri?.let { uri ->
            applyVideoUri(view, uri)
        }
    }

    fun unbindView() {
        try {
            videoView?.stopPlayback()
        } catch (_: Exception) {
            // View may already be torn down
        }
        videoView = null
        isVideoPlaying = false
        emit()
    }

    fun onUrlChange(text: String) {
        videoUrl = text
        emit()
    }

    fun openUrl() {
        val trimmed = videoUrl.trim()
        if (trimmed.isEmpty()) {
            videoStatus = "No file selected"
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
        isVideoLoaded = false
        isVideoPlaying = false
        videoStatus = "Stopped"
        emit()
        val vv = videoView
        if (vv != null) {
            applyVideoUri(vv, uri)
        }
    }

    private fun applyVideoUri(vv: VideoView, uri: Uri) {
        try {
            vv.setOnPreparedListener {
                isVideoLoaded = true
                isVideoPlaying = false
                videoStatus = "Ready"
                emit()
            }
            vv.setVideoURI(uri)
        } catch (_: Exception) {
            isVideoLoaded = false
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }

    fun play() {
        if (videoUri == null) {
            videoStatus = "No file selected"
            emit()
            return
        }
        if (!isVideoLoaded) {
            videoStatus = "No file selected"
            emit()
            return
        }
        val vv = videoView ?: run {
            videoStatus = "No file selected"
            emit()
            return
        }
        try {
            vv.start()
            isVideoPlaying = true
            videoStatus = "Playing"
            emit()
        } catch (_: Exception) {
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }

    fun pause() {
        if (!isVideoPlaying) return
        val vv = videoView ?: return
        try {
            vv.pause()
            isVideoPlaying = false
            videoStatus = "Paused"
            emit()
        } catch (_: Exception) {
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }

    fun stop() {
        val uri = videoUri ?: return
        val vv = videoView ?: return
        try {
            vv.stopPlayback()
        } catch (_: Exception) {
            return
        }
        isVideoPlaying = false
        try {
            vv.setOnPreparedListener {
                isVideoLoaded = true
                isVideoPlaying = false
                videoStatus = "Stopped"
                emit()
            }
            vv.setVideoURI(uri)
        } catch (_: Exception) {
            isVideoLoaded = false
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }

    fun restart() {
        if (videoUri == null) {
            videoStatus = "No file selected"
            emit()
            return
        }
        if (!isVideoLoaded) {
            videoStatus = "No file selected"
            emit()
            return
        }
        val vv = videoView ?: run {
            videoStatus = "No file selected"
            emit()
            return
        }
        try {
            @Suppress("DEPRECATION")
            vv.seekTo(0)
            vv.start()
            isVideoPlaying = true
            videoStatus = "Playing"
            emit()
        } catch (_: Exception) {
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }
}

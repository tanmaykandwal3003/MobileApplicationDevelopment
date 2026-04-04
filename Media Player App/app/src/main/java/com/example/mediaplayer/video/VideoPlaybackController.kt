package com.example.mediaplayer.video

import android.content.Context
import android.net.Uri
import android.widget.Toast
import android.widget.VideoView

class VideoPlaybackController(
    private val context: Context,
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

    private fun toast(message: String) {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun formatUrlDisplay(raw: String): String {
        val t = raw.trim()
        if (t.length <= 52) return t
        return t.take(25) + "…" + t.takeLast(24)
    }

    private fun emit() {
        onState(
            VideoUiState(
                videoUrl = videoUrl,
                videoUri = videoUri,
                videoUrlDisplay = formatUrlDisplay(videoUrl),
                videoStatus = videoStatus,
                isVideoPlaying = isVideoPlaying,
                isVideoLoaded = isVideoLoaded,
                isVideoBound = videoView != null
            )
        )
    }

    private fun isValidStreamUrl(trimmed: String): Boolean {
        if (trimmed.isEmpty()) return false
        val lower = trimmed.lowercase()
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            return false
        }
        val uri = try {
            Uri.parse(trimmed)
        } catch (_: Exception) {
            return false
        }
        return !uri.scheme.isNullOrBlank() && !uri.host.isNullOrBlank()
    }

    fun bindView(view: VideoView) {
        videoView = view
        view.setOnErrorListener { _, _, _ ->
            toast("Error loading video")
            isVideoPlaying = false
            isVideoLoaded = false
            videoStatus = "Stopped"
            try {
                view.stopPlayback()
            } catch (_: Exception) {
                // ignore
            }
            emit()
            true
        }
        view.setOnCompletionListener {
            isVideoPlaying = false
            videoStatus = if (videoUri != null) "Ready" else "No file selected"
            emit()
        }
        videoUri?.let { uri ->
            videoStatus = "Loading..."
            emit()
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
        if (!isValidStreamUrl(trimmed)) {
            toast("Please enter a valid video URL")
            if (trimmed.isEmpty()) {
                videoStatus = "No file selected"
            } else {
                videoStatus = "Stopped"
            }
            emit()
            return
        }
        val uri = Uri.parse(trimmed)
        videoUri = uri
        isVideoLoaded = false
        isVideoPlaying = false
        videoStatus = "Loading..."
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
            toast("Error loading video")
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
            toast("Error loading video")
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
            toast("Error loading video")
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
        videoStatus = "Loading..."
        emit()
        try {
            vv.setOnPreparedListener {
                isVideoLoaded = true
                isVideoPlaying = false
                videoStatus = "Stopped"
                emit()
            }
            vv.setVideoURI(uri)
        } catch (_: Exception) {
            toast("Error loading video")
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
            toast("Error loading video")
            isVideoPlaying = false
            videoStatus = "Stopped"
            emit()
        }
    }
}

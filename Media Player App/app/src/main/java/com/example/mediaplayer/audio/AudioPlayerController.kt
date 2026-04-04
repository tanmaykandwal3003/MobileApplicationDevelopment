package com.example.mediaplayer.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
class AudioPlayerController(
    private val context: Context,
    private val onState: (AudioUiState) -> Unit
) {

    private var mediaPlayer: MediaPlayer? = null
    private var selectedAudioUri: Uri? = null
    private var status: String = "No file selected"
    private var isPlaying: Boolean = false

    init {
        emit()
    }

    private fun emit() {
        onState(
            AudioUiState(
                selectedAudioUri = selectedAudioUri,
                isPlaying = isPlaying,
                status = status
            )
        )
    }

    fun loadAudio(uri: Uri) {
        releasePlayer()
        selectedAudioUri = uri
        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(context.applicationContext, uri)
            player.setOnPreparedListener {
                status = "Ready to play"
                isPlaying = false
                emit()
            }
            player.setOnCompletionListener {
                isPlaying = false
                status = "Ready to play"
                emit()
            }
            player.setOnErrorListener { _, _, _ ->
                isPlaying = false
                status = "Invalid file"
                releasePlayer()
                emit()
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            isPlaying = false
            status = "Invalid file"
            selectedAudioUri = null
            releasePlayer()
            emit()
        }
    }

    fun play() {
        if (selectedAudioUri == null) {
            status = "No file selected"
            emit()
            return
        }
        val player = mediaPlayer ?: run {
            status = if (selectedAudioUri == null) "No file selected" else "Invalid file"
            emit()
            return
        }
        try {
            player.start()
            isPlaying = true
            status = "Playing"
            emit()
        } catch (_: IllegalStateException) {
            isPlaying = false
            status = "Invalid file"
            emit()
        }
    }

    fun pause() {
        val player = mediaPlayer ?: return
        try {
            if (player.isPlaying) {
                player.pause()
                isPlaying = false
                status = "Paused"
                emit()
            }
        } catch (_: IllegalStateException) {
            isPlaying = false
            status = "Invalid file"
            emit()
        }
    }

    fun stop() {
        val player = mediaPlayer ?: run {
            status = "No file selected"
            emit()
            return
        }
        try {
            player.stop()
        } catch (_: IllegalStateException) {
            return
        }
        try {
            player.setOnPreparedListener {
                isPlaying = false
                status = "Stopped"
                emit()
            }
            player.prepareAsync()
        } catch (_: Exception) {
            isPlaying = false
            status = "Invalid file"
            emit()
        }
    }

    fun restart() {
        if (selectedAudioUri == null) {
            status = "No file selected"
            emit()
            return
        }
        val player = mediaPlayer ?: run {
            status = if (selectedAudioUri == null) "No file selected" else "Invalid file"
            emit()
            return
        }
        try {
            @Suppress("DEPRECATION")
            player.seekTo(0)
            player.start()
            isPlaying = true
            status = "Playing"
            emit()
        } catch (_: Exception) {
            isPlaying = false
            status = "Invalid file"
            emit()
        }
    }

    fun release() {
        releasePlayer()
        isPlaying = false
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

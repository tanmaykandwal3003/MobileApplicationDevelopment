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
    private var isPrepared: Boolean = false
    private var released: Boolean = false

    init {
        emit()
    }

    private fun emit() {
        if (released) return
        val player = mediaPlayer
        val loaded = isPrepared && player != null && selectedAudioUri != null
        onState(
            AudioUiState(
                selectedAudioUri = selectedAudioUri,
                audioStatus = status,
                isAudioPlaying = isPlaying,
                isAudioLoaded = loaded,
                isAudioInitialized = player != null
            )
        )
    }

    fun loadAudio(uri: Uri) {
        if (released) return
        releasePlayer()
        selectedAudioUri = uri
        isPrepared = false
        isPlaying = false
        status = "Stopped"
        emit()
        try {
            val player = MediaPlayer()
            mediaPlayer = player
            player.setDataSource(context.applicationContext, uri)
            player.setOnPreparedListener {
                if (released) return@setOnPreparedListener
                isPrepared = true
                isPlaying = false
                status = "Ready"
                emit()
            }
            player.setOnCompletionListener {
                if (released) return@setOnCompletionListener
                isPlaying = false
                status = "Ready"
                emit()
            }
            player.setOnErrorListener { _, _, _ ->
                if (released) return@setOnErrorListener true
                isPlaying = false
                isPrepared = false
                status = "Stopped"
                selectedAudioUri = null
                releasePlayer()
                emit()
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            isPlaying = false
            isPrepared = false
            status = "Stopped"
            selectedAudioUri = null
            releasePlayer()
            emit()
        }
    }

    fun play() {
        if (released) return
        if (selectedAudioUri == null) {
            status = "No file selected"
            emit()
            return
        }
        val player = mediaPlayer
        if (player == null || !isPrepared) {
            status = "No file selected"
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
            isPrepared = false
            status = "Stopped"
            releasePlayer()
            emit()
        }
    }

    fun pause() {
        if (released) return
        val player = mediaPlayer ?: return
        if (!isPlaying) return
        try {
            if (player.isPlaying) {
                player.pause()
            }
            isPlaying = false
            status = "Paused"
            emit()
        } catch (_: IllegalStateException) {
            isPlaying = false
            isPrepared = false
            status = "Stopped"
            releasePlayer()
            emit()
        }
    }

    fun stop() {
        if (released) return
        val player = mediaPlayer ?: return
        try {
            player.stop()
        } catch (_: IllegalStateException) {
            return
        }
        isPlaying = false
        try {
            player.setOnPreparedListener {
                if (released) return@setOnPreparedListener
                isPrepared = true
                isPlaying = false
                status = "Stopped"
                emit()
            }
            player.prepareAsync()
        } catch (_: Exception) {
            isPrepared = false
            status = "Stopped"
            releasePlayer()
            emit()
        }
    }

    fun restart() {
        if (released) return
        if (selectedAudioUri == null) {
            status = "No file selected"
            emit()
            return
        }
        val player = mediaPlayer
        if (player == null || !isPrepared) {
            status = "No file selected"
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
            isPrepared = false
            status = "Stopped"
            releasePlayer()
            emit()
        }
    }

    fun release() {
        if (released) return
        released = true
        releasePlayer()
        isPlaying = false
        isPrepared = false
    }

    private fun releasePlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}

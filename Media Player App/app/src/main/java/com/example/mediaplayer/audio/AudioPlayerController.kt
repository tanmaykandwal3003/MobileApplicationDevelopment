package com.example.mediaplayer.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast

class AudioPlayerController(
    private val context: Context,
    private val onState: (AudioUiState) -> Unit
) {

    private var mediaPlayer: MediaPlayer? = null
    private var selectedAudioUri: Uri? = null
    private var audioDisplayName: String? = null
    private var status: String = "No file selected"
    private var isPlaying: Boolean = false
    private var isPrepared: Boolean = false
    private var released: Boolean = false

    init {
        emit()
    }

    private fun toast(message: String) {
        Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT).show()
    }

    private fun resolveDisplayName(uri: Uri): String? {
        return try {
            context.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME),
                null,
                null,
                null
            )?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) cursor.getString(idx) else null
                } else {
                    null
                }
            } ?: uri.lastPathSegment
        } catch (_: Exception) {
            uri.lastPathSegment
        }
    }

    private fun emit() {
        if (released) return
        val player = mediaPlayer
        val loaded = isPrepared && player != null && selectedAudioUri != null
        onState(
            AudioUiState(
                selectedAudioUri = selectedAudioUri,
                audioDisplayName = audioDisplayName,
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
        audioDisplayName = resolveDisplayName(uri)
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
                status = "Completed"
                emit()
            }
            player.setOnErrorListener { _, _, _ ->
                if (released) return@setOnErrorListener true
                toast("Error playing audio")
                isPlaying = false
                isPrepared = false
                status = "Stopped"
                selectedAudioUri = null
                audioDisplayName = null
                releasePlayer()
                emit()
                true
            }
            player.prepareAsync()
        } catch (_: Exception) {
            toast("Error playing audio")
            isPlaying = false
            isPrepared = false
            status = "Stopped"
            selectedAudioUri = null
            audioDisplayName = null
            releasePlayer()
            emit()
        }
    }

    fun play() {
        if (released) return
        if (selectedAudioUri == null || mediaPlayer == null || !isPrepared) {
            toast("Please select an audio file first")
            if (selectedAudioUri == null) {
                status = "No file selected"
            }
            emit()
            return
        }
        val player = mediaPlayer ?: return
        try {
            if (status == "Completed") {
                @Suppress("DEPRECATION")
                player.seekTo(0)
            }
            player.start()
            isPlaying = true
            status = "Playing"
            emit()
        } catch (_: IllegalStateException) {
            toast("Error playing audio")
            isPlaying = false
            isPrepared = false
            status = "Stopped"
            selectedAudioUri = null
            audioDisplayName = null
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
            toast("Error playing audio")
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
            toast("Error playing audio")
            isPrepared = false
            status = "Stopped"
            releasePlayer()
            emit()
        }
    }

    fun restart() {
        if (released) return
        if (selectedAudioUri == null || mediaPlayer == null || !isPrepared) {
            toast("Please select an audio file first")
            emit()
            return
        }
        val player = mediaPlayer ?: return
        try {
            @Suppress("DEPRECATION")
            player.seekTo(0)
            player.start()
            isPlaying = true
            status = "Playing"
            emit()
        } catch (_: Exception) {
            toast("Error playing audio")
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

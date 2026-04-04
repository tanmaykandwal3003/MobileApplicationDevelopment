package com.example.mediaplayer.playback

import com.example.mediaplayer.audio.AudioPlayerController
import com.example.mediaplayer.audio.AudioUiState
import com.example.mediaplayer.video.VideoPlaybackController
import com.example.mediaplayer.video.VideoUiState

/**
 * Coordinates audio and video so only one stream plays at a time.
 */
class PlaybackCoordinator(
    private val audio: AudioPlayerController,
    private val video: VideoPlaybackController,
    private val currentAudioState: () -> AudioUiState,
    private val currentVideoState: () -> VideoUiState
) {

    fun playAudio() {
        if (currentVideoState().isVideoPlaying) {
            video.stop()
        }
        audio.play()
    }

    fun pauseAudio() {
        audio.pause()
    }

    fun stopAudio() {
        audio.stop()
    }

    fun restartAudio() {
        if (currentVideoState().isVideoPlaying) {
            video.stop()
        }
        audio.restart()
    }

    fun playVideo() {
        if (currentAudioState().isAudioPlaying) {
            audio.stop()
        }
        video.play()
    }

    fun pauseVideo() {
        video.pause()
    }

    fun stopVideo() {
        video.stop()
    }

    fun restartVideo() {
        if (currentAudioState().isAudioPlaying) {
            audio.stop()
        }
        video.restart()
    }
}

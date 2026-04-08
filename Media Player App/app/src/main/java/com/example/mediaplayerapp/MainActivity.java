package com.example.mediaplayerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class MainActivity extends AppCompatActivity {
    private TextView tvAudioFile;
    private TextView tvAudioStatus;
    private TextView tvVideoUrl;
    private TextView tvVideoStatus;
    private EditText etVideoUrl;
    private PlayerView playerView;
    private Button btnAudioPlay;
    private Button btnAudioPause;
    private Button btnAudioStop;
    private Button btnAudioRestart;
    private Button btnVideoPlay;
    private Button btnVideoPause;
    private Button btnVideoStop;
    private Button btnVideoRestart;

    private Uri selectedAudioUri;
    private Uri selectedVideoUri;
    private MediaPlayer audioPlayer;
    private ExoPlayer exoPlayer;

    private ActivityResultLauncher<String[]> pickAudioLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE);
        boolean darkModeEnabled = preferences.getBoolean(SettingsActivity.KEY_DARK_MODE, true);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setupAudioPicker();
        setupClickListeners();
        setupVideoCallbacks();
    }

    private void initializeViews() {
        tvAudioFile = findViewById(R.id.tvAudioFile);
        tvAudioStatus = findViewById(R.id.tvAudioStatus);
        tvVideoUrl = findViewById(R.id.tvVideoUrl);
        tvVideoStatus = findViewById(R.id.tvVideoStatus);
        etVideoUrl = findViewById(R.id.etVideoUrl);
        playerView = findViewById(R.id.playerView);

        btnAudioPlay = findViewById(R.id.btnAudioPlay);
        btnAudioPause = findViewById(R.id.btnAudioPause);
        btnAudioStop = findViewById(R.id.btnAudioStop);
        btnAudioRestart = findViewById(R.id.btnAudioRestart);
        btnVideoPlay = findViewById(R.id.btnVideoPlay);
        btnVideoPause = findViewById(R.id.btnVideoPause);
        btnVideoStop = findViewById(R.id.btnVideoStop);
        btnVideoRestart = findViewById(R.id.btnVideoRestart);

        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);
    }

    private void setupAudioPicker() {
        pickAudioLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri == null) {
                        return;
                    }
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedAudioUri = uri;
                    tvAudioFile.setText("File: " + uri.getLastPathSegment());
                    tvAudioStatus.setText("Audio file selected");
                    setAudioButtonsEnabled(true);
                });
    }

    private void setupClickListeners() {
        ImageButton btnSettings = findViewById(R.id.btnSettings);
        Button btnOpenFile = findViewById(R.id.btnOpenFile);
        Button btnOpenUrl = findViewById(R.id.btnOpenUrl);

        btnSettings.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)));
        btnOpenFile.setOnClickListener(v -> pickAudioLauncher.launch(new String[]{"audio/*"}));

        btnOpenUrl.setOnClickListener(v -> {
            String url = etVideoUrl.getText().toString().trim();
            if (url.isEmpty()) {
                Toast.makeText(this, "Please enter a video URL", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedVideoUri = Uri.parse(url);
            tvVideoUrl.setText("URL: " + url);
            tvVideoStatus.setText("Video URL loaded");
            exoPlayer.setMediaItem(MediaItem.fromUri(selectedVideoUri));
            exoPlayer.prepare();
            setVideoButtonsEnabled(true);
        });

        btnAudioPlay.setOnClickListener(v -> playAudio());
        btnAudioPause.setOnClickListener(v -> pauseAudio());
        btnAudioStop.setOnClickListener(v -> stopAudio());
        btnAudioRestart.setOnClickListener(v -> restartAudio());

        btnVideoPlay.setOnClickListener(v -> playVideo());
        btnVideoPause.setOnClickListener(v -> pauseVideo());
        btnVideoStop.setOnClickListener(v -> stopVideo());
        btnVideoRestart.setOnClickListener(v -> restartVideo());
    }

    private void setupVideoCallbacks() {
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && exoPlayer.getPlayWhenReady()) {
                    tvVideoStatus.setText("Video playing");
                } else if (playbackState == Player.STATE_ENDED) {
                    tvVideoStatus.setText("Video completed");
                } else if (playbackState == Player.STATE_READY) {
                    tvVideoStatus.setText("Video ready to play");
                }
            }

            @Override
            public void onPlayerError(androidx.media3.common.PlaybackException error) {
                tvVideoStatus.setText("Unable to play video");
                Toast.makeText(MainActivity.this, "Failed to stream video from URL", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playAudio() {
        if (selectedAudioUri == null) {
            return;
        }
        try {
            if (audioPlayer == null) {
                audioPlayer = new MediaPlayer();
                audioPlayer.setDataSource(this, selectedAudioUri);
                audioPlayer.prepare();
                audioPlayer.setOnCompletionListener(mp -> tvAudioStatus.setText("Audio completed"));
            }
            audioPlayer.start();
            tvAudioStatus.setText("Audio playing");
        } catch (Exception e) {
            tvAudioStatus.setText("Unable to play selected audio");
            Toast.makeText(this, "Error playing audio file", Toast.LENGTH_SHORT).show();
        }
    }

    private void pauseAudio() {
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
            tvAudioStatus.setText("Audio paused");
        }
    }

    private void stopAudio() {
        if (audioPlayer != null) {
            if (audioPlayer.isPlaying()) {
                audioPlayer.stop();
            }
            audioPlayer.release();
            audioPlayer = null;
            tvAudioStatus.setText("Audio stopped");
        }
    }

    private void restartAudio() {
        stopAudio();
        playAudio();
    }

    private void playVideo() {
        if (selectedVideoUri == null || exoPlayer == null) {
            return;
        }
        exoPlayer.play();
        tvVideoStatus.setText("Video playing");
    }

    private void pauseVideo() {
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            exoPlayer.pause();
            tvVideoStatus.setText("Video paused");
        }
    }

    private void stopVideo() {
        if (selectedVideoUri != null && exoPlayer != null) {
            exoPlayer.pause();
            exoPlayer.seekTo(0);
            tvVideoStatus.setText("Video stopped");
        }
    }

    private void restartVideo() {
        if (selectedVideoUri != null && exoPlayer != null) {
            exoPlayer.seekTo(0);
            exoPlayer.play();
            tvVideoStatus.setText("Video restarted");
        }
    }

    private void setAudioButtonsEnabled(boolean enabled) {
        btnAudioPlay.setEnabled(enabled);
        btnAudioPause.setEnabled(enabled);
        btnAudioStop.setEnabled(enabled);
        btnAudioRestart.setEnabled(enabled);
    }

    private void setVideoButtonsEnabled(boolean enabled) {
        btnVideoPlay.setEnabled(enabled);
        btnVideoPause.setEnabled(enabled);
        btnVideoStop.setEnabled(enabled);
        btnVideoRestart.setEnabled(enabled);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioPlayer != null && audioPlayer.isPlaying()) {
            audioPlayer.pause();
        }
        if (exoPlayer != null && exoPlayer.isPlaying()) {
            pauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioPlayer != null) {
            audioPlayer.release();
            audioPlayer = null;
        }
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
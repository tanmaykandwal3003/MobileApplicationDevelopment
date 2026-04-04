package com.example.mediaplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mediaplayer.ui.MediaPlayerScreen
import com.example.mediaplayer.ui.theme.MediaPlayerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MediaPlayerScreen()
                }
            }
        }
    }
}

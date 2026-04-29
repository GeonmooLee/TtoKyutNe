package com.example.ttokyutne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ttokyutne.ui.home.HomeScreen
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TtoKyutNeTheme {
                HomeScreen()
            }
        }
    }
}

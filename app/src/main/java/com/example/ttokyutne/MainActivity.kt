package com.example.ttokyutne

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ttokyutne.ui.home.HomeScreen
import com.example.ttokyutne.ui.home.HomeViewModel
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TtoKyutNeTheme {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = HomeViewModel.Factory(applicationContext)
                )
                val uiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onRecordTestEvent = homeViewModel::recordTestEvent
                )
            }
        }
    }
}

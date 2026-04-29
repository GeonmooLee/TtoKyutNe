package com.example.ttokyutne

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.ttokyutne.ui.home.HomeScreen
import com.example.ttokyutne.ui.home.HomeViewModel
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme
import com.example.ttokyutne.monitor.ScreenMonitorService
import androidx.lifecycle.ViewModelProvider

private const val LOG_TAG = "Ttokyeonne"

class MainActivity : ComponentActivity() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModel.Factory(applicationContext)
        )[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            TtoKyutNeTheme {
                val uiState by homeViewModel.uiState.collectAsState()

                HomeScreen(
                    uiState = uiState,
                    onRecordTestEvent = homeViewModel::recordTestEvent,
                    onStartScreenMonitor = ::startScreenMonitorService
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::homeViewModel.isInitialized) {
            homeViewModel.refreshTodayStats()
        }
    }

    private fun startScreenMonitorService() {
        val intent = Intent(this, ScreenMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
        Log.d(LOG_TAG, "Requested ScreenMonitorService start")
    }
}

package com.example.ttokyutne

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.ttokyutne.monitor.ScreenMonitorService
import com.example.ttokyutne.ui.home.HomeScreen
import com.example.ttokyutne.ui.home.HomeViewModel
import com.example.ttokyutne.ui.theme.TtoKyutNeTheme

private const val LOG_TAG = "Ttokyeonne"

class MainActivity : ComponentActivity() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>
    private var notificationPermissionGranted by mutableStateOf(true)
    private var pendingStartAfterNotificationPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            notificationPermissionGranted = isNotificationPermissionGranted()
            Log.d(LOG_TAG, "POST_NOTIFICATIONS permission granted=$isGranted")
            if (isGranted && pendingStartAfterNotificationPermission) {
                doStartScreenMonitorService()
            }
            pendingStartAfterNotificationPermission = false
        }
        notificationPermissionGranted = isNotificationPermissionGranted()

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
                    notificationPermissionGranted = notificationPermissionGranted,
                    onRecordTestEvent = homeViewModel::recordTestEvent,
                    onStartScreenMonitor = ::startScreenMonitorService,
                    onRequestNotificationPermission = ::requestNotificationPermission
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        notificationPermissionGranted = isNotificationPermissionGranted()
        if (::homeViewModel.isInitialized) {
            homeViewModel.refreshTodayStats()
        }
    }

    private fun startScreenMonitorService() {
        if (!isNotificationPermissionGranted()) {
            pendingStartAfterNotificationPermission = true
            requestNotificationPermission()
            Log.d(LOG_TAG, "ScreenMonitorService start delayed: notification permission missing")
            return
        }

        doStartScreenMonitorService()
    }

    private fun doStartScreenMonitorService() {
        val intent = Intent(this, ScreenMonitorService::class.java)
        ContextCompat.startForegroundService(this, intent)
        Log.d(LOG_TAG, "Requested ScreenMonitorService start")
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionGranted = true
            return
        }

        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun isNotificationPermissionGranted(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }
}

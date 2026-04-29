package com.example.ttokyutne.monitor

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.ttokyutne.data.local.AppDatabase
import com.example.ttokyutne.data.repository.ScreenOnEventRepository
import com.example.ttokyutne.data.repository.SettingsRepository
import com.example.ttokyutne.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val LOG_TAG = "Ttokyeonne"
private const val MONITOR_NOTIFICATION_ID = 1001

class ScreenMonitorService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val notificationHelper by lazy { NotificationHelper(this) }
    private val database by lazy { AppDatabase.getInstance(applicationContext) }
    private val screenOnEventRepository by lazy {
        ScreenOnEventRepository(database.screenOnEventDao())
    }
    private val settingsRepository by lazy {
        SettingsRepository(
            userSettingsDao = database.userSettingsDao(),
            screenOnEventDao = database.screenOnEventDao(),
            phraseHistoryDao = database.phraseHistoryDao()
        )
    }
    private var receiverRegistered = false

    private val screenOnReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Intent.ACTION_SCREEN_ON) return

            serviceScope.launch {
                val recordedEvent = screenOnEventRepository.recordScreenOnEvent()
                Log.d(
                    LOG_TAG,
                    "ACTION_SCREEN_ON saved id=${recordedEvent.id}, intervalSeconds=${recordedEvent.intervalSeconds}"
                )
                showRecheckAlertIfNeeded(recordedEvent.intervalSeconds)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationHelper.createNotificationChannels()
        startAsForegroundService()
        registerScreenOnReceiver()
        Log.d(LOG_TAG, "ScreenMonitorService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(LOG_TAG, "ScreenMonitorService started")
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterScreenOnReceiver()
        serviceScope.cancel()
        Log.d(LOG_TAG, "ScreenMonitorService destroyed")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun registerScreenOnReceiver() {
        if (receiverRegistered) return

        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(screenOnReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            @Suppress("DEPRECATION")
            registerReceiver(screenOnReceiver, filter)
        }
        receiverRegistered = true
        Log.d(LOG_TAG, "ACTION_SCREEN_ON receiver registered")
    }

    private fun unregisterScreenOnReceiver() {
        if (!receiverRegistered) return

        unregisterReceiver(screenOnReceiver)
        receiverRegistered = false
        Log.d(LOG_TAG, "ACTION_SCREEN_ON receiver unregistered")
    }

    private fun startAsForegroundService() {
        val notification = notificationHelper.buildMonitorNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                MONITOR_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(MONITOR_NOTIFICATION_ID, notification)
        }
    }

    private suspend fun showRecheckAlertIfNeeded(intervalSeconds: Long?) {
        when {
            intervalSeconds == null -> {
                Log.d(LOG_TAG, "Recheck alert skipped: first screen-on event")
            }

            else -> {
                val settings = settingsRepository.getSettings()
                if (!settings.notificationEnabled) {
                    Log.d(LOG_TAG, "Recheck alert skipped: notificationEnabled=false")
                    return
                }

                if (intervalSeconds > settings.minIntervalSeconds) {
                    Log.d(
                        LOG_TAG,
                        "Recheck alert skipped: intervalSeconds=$intervalSeconds, minIntervalSeconds=${settings.minIntervalSeconds}"
                    )
                    return
                }

                val shown = notificationHelper.showRecheckAlert(intervalSeconds)
                Log.d(
                    LOG_TAG,
                    "Recheck alert ${if (shown) "shown" else "skipped: notification permission missing"}, intervalSeconds=$intervalSeconds, minIntervalSeconds=${settings.minIntervalSeconds}"
                )
            }
        }
    }
}

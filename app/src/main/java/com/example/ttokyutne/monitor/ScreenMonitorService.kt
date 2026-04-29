package com.example.ttokyutne.monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ttokyutne.MainActivity
import com.example.ttokyutne.data.local.AppDatabase
import com.example.ttokyutne.data.repository.ScreenOnEventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val LOG_TAG = "Ttokyeonne"
private const val NOTIFICATION_CHANNEL_ID = "screen_monitor"
private const val NOTIFICATION_ID = 1001

class ScreenMonitorService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val screenOnEventRepository by lazy {
        ScreenOnEventRepository(
            AppDatabase.getInstance(applicationContext).screenOnEventDao()
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
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
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
        val notification = buildForegroundNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("또켰네 실행 중")
            .setContentText("또켰네가 화면 재확인 간격을 기록 중입니다")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "또켰네 실행 중",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "또켰네가 화면 재확인 간격을 기록 중입니다"
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}

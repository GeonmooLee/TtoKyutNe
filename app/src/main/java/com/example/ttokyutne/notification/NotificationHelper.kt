package com.example.ttokyutne.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.ttokyutne.MainActivity

private const val MONITOR_CHANNEL_ID = "screen_monitor"
private const val RECHECK_ALERT_CHANNEL_ID = "recheck_alert_channel"
private const val RECHECK_ALERT_NOTIFICATION_ID = 2001

class NotificationHelper(
    private val context: Context
) {
    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val monitorChannel = NotificationChannel(
            MONITOR_CHANNEL_ID,
            "또켰네 실행 중",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "또켰네가 화면 재확인 간격을 기록 중입니다"
        }

        val recheckAlertChannel = NotificationChannel(
            RECHECK_ALERT_CHANNEL_ID,
            "또켰네 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "짧은 간격으로 화면을 다시 켰을 때 표시되는 알림입니다"
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        notificationManager.createNotificationChannel(monitorChannel)
        notificationManager.createNotificationChannel(recheckAlertChannel)
    }

    fun buildMonitorNotification(): Notification {
        return NotificationCompat.Builder(context, MONITOR_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("또켰네 실행 중")
            .setContentText("또켰네가 화면 재확인 간격을 기록 중입니다")
            .setContentIntent(createMainActivityPendingIntent(100))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    @SuppressLint("MissingPermission")
    fun showRecheckAlert(contentText: String): Boolean {
        if (!canPostNotifications()) return false

        val now = System.currentTimeMillis()
        val notification = NotificationCompat.Builder(context, RECHECK_ALERT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("또켰네")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setContentIntent(createMainActivityPendingIntent(200))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setWhen(now)
            .setShowWhen(true)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(RECHECK_ALERT_NOTIFICATION_ID, notification)
        return true
    }

    fun canPostNotifications(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createMainActivityPendingIntent(requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}

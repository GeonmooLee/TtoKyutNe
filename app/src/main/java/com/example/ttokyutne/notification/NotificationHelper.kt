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
    fun showRecheckAlert(intervalSeconds: Long): Boolean {
        if (!canPostNotifications()) return false

        val now = System.currentTimeMillis()
        val contentText = buildRecheckMessage(intervalSeconds, now)
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

    private fun buildRecheckMessage(intervalSeconds: Long, nowMillis: Long): String {
        val interval = formatIntervalSeconds(intervalSeconds)
        val phrase = recheckPhrases[(nowMillis / 1000L % recheckPhrases.size).toInt()]
        return if (phrase.contains("{interval}")) {
            phrase.replace("{interval}", interval)
        } else {
            "$interval 만에 다시 켰어요. $phrase"
        }
    }

    private fun formatIntervalSeconds(intervalSeconds: Long): String {
        return when {
            intervalSeconds < 60 -> "${intervalSeconds}초"
            else -> {
                val minutes = intervalSeconds / 60
                val seconds = intervalSeconds % 60
                if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
            }
        }
    }

    private val recheckPhrases = listOf(
        "{interval} 만에 다시 켰어요. 지금 필요한 건 정보일까요, 안심일까요?",
        "방금 확인했는데 다시 켰네요. 혹시 마음이 조금 불안했나요?",
        "조금 전에도 확인했어요. 지금은 잠깐 쉬어도 괜찮아요.",
        "새 알림이 없어도 확인하고 싶을 때가 있죠. 그 마음을 알아차려봐요.",
        "지금 필요한 건 새 정보보다 다시 집중하는 힘일지도 몰라요."
    )
}

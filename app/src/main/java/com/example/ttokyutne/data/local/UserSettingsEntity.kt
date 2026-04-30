package com.example.ttokyutne.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val notificationEnabled: Boolean = true,
    val minIntervalSeconds: Long = 600,
    @ColumnInfo(defaultValue = "1")
    val vibrationEnabled: Boolean = true,
    @ColumnInfo(defaultValue = "'WITH_PHRASE'")
    val recheckAlertMode: String = "WITH_PHRASE",
    val quietHoursEnabled: Boolean = false,
    val dataRetentionDays: Int = 365,
    val updatedAt: Long
)

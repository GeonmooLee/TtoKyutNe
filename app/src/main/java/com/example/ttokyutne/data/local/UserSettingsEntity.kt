package com.example.ttokyutne.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val notificationEnabled: Boolean = true,
    val minIntervalSeconds: Long = 600,
    val quietHoursEnabled: Boolean = false,
    val dataRetentionDays: Int = 365,
    val updatedAt: Long
)

package com.example.ttokyutne.ui.settings

data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val minIntervalSeconds: Long = 600,
    val quietHoursEnabled: Boolean = false,
    val dataRetentionDays: Int = 365
)

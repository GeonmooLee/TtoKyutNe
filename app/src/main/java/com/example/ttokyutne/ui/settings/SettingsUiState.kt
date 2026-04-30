package com.example.ttokyutne.ui.settings

import com.example.ttokyutne.settings.RecheckAlertMode

data class SettingsUiState(
    val notificationEnabled: Boolean = true,
    val minIntervalSeconds: Long = 600,
    val vibrationEnabled: Boolean = true,
    val recheckAlertMode: RecheckAlertMode = RecheckAlertMode.WithPhrase,
    val quietHoursEnabled: Boolean = false,
    val dataRetentionDays: Int = 365
)

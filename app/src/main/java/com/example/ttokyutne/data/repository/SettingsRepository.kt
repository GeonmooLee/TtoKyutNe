package com.example.ttokyutne.data.repository

import com.example.ttokyutne.data.local.PhraseHistoryDao
import com.example.ttokyutne.data.local.ScreenOnEventDao
import com.example.ttokyutne.data.local.UserSettingsDao
import com.example.ttokyutne.data.local.UserSettingsEntity
import com.example.ttokyutne.settings.RecheckAlertMode

class SettingsRepository(
    private val userSettingsDao: UserSettingsDao,
    private val screenOnEventDao: ScreenOnEventDao,
    private val phraseHistoryDao: PhraseHistoryDao
) {
    suspend fun getSettings(): UserSettingsEntity {
        return userSettingsDao.getSettings() ?: defaultSettings().also {
            userSettingsDao.upsertSettings(it)
        }
    }

    suspend fun updateNotificationEnabled(enabled: Boolean): UserSettingsEntity {
        val current = getSettings()
        val updated = current.copy(
            notificationEnabled = enabled,
            recheckAlertMode = if (enabled) {
                RecheckAlertMode.WithPhrase.storageValue
            } else {
                RecheckAlertMode.Simple.storageValue
            },
            updatedAt = System.currentTimeMillis()
        )
        userSettingsDao.upsertSettings(updated)
        return updated
    }

    suspend fun updateRecheckAlertMode(mode: RecheckAlertMode): UserSettingsEntity {
        val current = getSettings()
        val updated = current.copy(
            notificationEnabled = mode != RecheckAlertMode.Off,
            recheckAlertMode = mode.storageValue,
            updatedAt = System.currentTimeMillis()
        )
        userSettingsDao.upsertSettings(updated)
        return updated
    }

    suspend fun updateMinIntervalSeconds(seconds: Long): UserSettingsEntity {
        val current = getSettings()
        val updated = current.copy(
            minIntervalSeconds = seconds,
            updatedAt = System.currentTimeMillis()
        )
        userSettingsDao.upsertSettings(updated)
        return updated
    }

    suspend fun updateVibrationEnabled(enabled: Boolean): UserSettingsEntity {
        val current = getSettings()
        val updated = current.copy(
            vibrationEnabled = enabled,
            updatedAt = System.currentTimeMillis()
        )
        userSettingsDao.upsertSettings(updated)
        return updated
    }

    suspend fun updateOnboardingCompleted(completed: Boolean): UserSettingsEntity {
        val current = getSettings()
        val updated = current.copy(
            onboardingCompleted = completed,
            updatedAt = System.currentTimeMillis()
        )
        userSettingsDao.upsertSettings(updated)
        return updated
    }

    suspend fun deleteAllAppData() {
        screenOnEventDao.deleteAllEvents()
        phraseHistoryDao.deleteAllPhraseHistory()
    }

    private fun defaultSettings(): UserSettingsEntity {
        return UserSettingsEntity(updatedAt = System.currentTimeMillis())
    }
}

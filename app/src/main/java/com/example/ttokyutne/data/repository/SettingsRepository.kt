package com.example.ttokyutne.data.repository

import com.example.ttokyutne.data.local.PhraseHistoryDao
import com.example.ttokyutne.data.local.ScreenOnEventDao
import com.example.ttokyutne.data.local.UserSettingsDao
import com.example.ttokyutne.data.local.UserSettingsEntity

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

    suspend fun deleteAllAppData() {
        screenOnEventDao.deleteAllEvents()
        phraseHistoryDao.deleteAllPhraseHistory()
    }

    private fun defaultSettings(): UserSettingsEntity {
        return UserSettingsEntity(updatedAt = System.currentTimeMillis())
    }
}

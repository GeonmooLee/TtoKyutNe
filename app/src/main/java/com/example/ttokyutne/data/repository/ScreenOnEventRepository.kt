package com.example.ttokyutne.data.repository

import com.example.ttokyutne.data.local.ScreenOnEventDao
import com.example.ttokyutne.data.local.ScreenOnEventEntity
import java.time.LocalDate
import java.time.ZoneId

class ScreenOnEventRepository(
    private val screenOnEventDao: ScreenOnEventDao
) {
    suspend fun recordTestEvent(nowMillis: Long = System.currentTimeMillis()): RecordedScreenOnEvent {
        val previousEvent = screenOnEventDao.getLastEvent()
        val previousScreenOnTime = previousEvent?.screenOnTime
        val intervalSeconds = previousScreenOnTime?.let { previous ->
            ((nowMillis - previous).coerceAtLeast(0L)) / 1000L
        }

        val insertedId = screenOnEventDao.insertEvent(
            ScreenOnEventEntity(
                screenOnTime = nowMillis,
                previousScreenOnTime = previousScreenOnTime,
                intervalSeconds = intervalSeconds,
                phraseId = null,
                createdAt = nowMillis
            )
        )

        return RecordedScreenOnEvent(
            id = insertedId,
            intervalSeconds = intervalSeconds
        )
    }

    suspend fun getLastEvent(): ScreenOnEventEntity? {
        return screenOnEventDao.getLastEvent()
    }

    suspend fun getTodayEvents(nowMillis: Long = System.currentTimeMillis()): List<ScreenOnEventEntity> {
        return screenOnEventDao.getTodayEvents(startOfDayMillis(nowMillis))
    }

    suspend fun deleteAllEvents() {
        screenOnEventDao.deleteAllEvents()
    }

    private fun startOfDayMillis(nowMillis: Long): Long {
        val zoneId = ZoneId.systemDefault()
        return LocalDate.ofInstant(
            java.time.Instant.ofEpochMilli(nowMillis),
            zoneId
        ).atStartOfDay(zoneId).toInstant().toEpochMilli()
    }
}

data class RecordedScreenOnEvent(
    val id: Long,
    val intervalSeconds: Long?
)

package com.example.ttokyutne.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScreenOnEventDao {
    @Insert
    suspend fun insertEvent(event: ScreenOnEventEntity): Long

    @Query("SELECT * FROM screen_on_event ORDER BY screenOnTime DESC, id DESC LIMIT 1")
    suspend fun getLastEvent(): ScreenOnEventEntity?

    @Query("SELECT * FROM screen_on_event WHERE screenOnTime >= :startOfDayMillis ORDER BY screenOnTime ASC, id ASC")
    suspend fun getTodayEvents(startOfDayMillis: Long): List<ScreenOnEventEntity>

    @Query("DELETE FROM screen_on_event")
    suspend fun deleteAllEvents()
}

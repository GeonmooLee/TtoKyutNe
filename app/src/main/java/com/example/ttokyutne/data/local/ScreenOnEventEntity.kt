package com.example.ttokyutne.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_on_event")
data class ScreenOnEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val screenOnTime: Long,
    val previousScreenOnTime: Long?,
    val intervalSeconds: Long?,
    val phraseId: Int?,
    val createdAt: Long
)

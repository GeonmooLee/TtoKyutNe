package com.example.ttokyutne.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phrase_history")
data class PhraseHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val phraseId: Int,
    val screenOnEventId: Long?,
    val shownAt: Long
)

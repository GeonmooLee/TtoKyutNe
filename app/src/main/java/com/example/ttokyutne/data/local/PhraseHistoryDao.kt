package com.example.ttokyutne.data.local

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PhraseHistoryDao {
    @Query("DELETE FROM phrase_history")
    suspend fun deleteAllPhraseHistory()
}

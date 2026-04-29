package com.example.ttokyutne.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PhraseHistoryDao {
    @Insert
    suspend fun insertPhraseHistory(history: PhraseHistoryEntity): Long

    @Query("SELECT phraseId FROM phrase_history ORDER BY shownAt DESC, id DESC LIMIT :limit")
    suspend fun getRecentPhraseIds(limit: Int): List<Int>

    @Query("DELETE FROM phrase_history")
    suspend fun deleteAllPhraseHistory()
}

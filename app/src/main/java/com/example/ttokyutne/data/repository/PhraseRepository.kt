package com.example.ttokyutne.data.repository

import com.example.ttokyutne.data.local.PhraseHistoryDao
import com.example.ttokyutne.data.local.PhraseHistoryEntity
import com.example.ttokyutne.phrase.LocalPhrase
import com.example.ttokyutne.phrase.PhraseCategory
import com.example.ttokyutne.phrase.PhraseLibrary
import java.time.Instant
import java.time.ZoneId

class PhraseRepository(
    private val phraseHistoryDao: PhraseHistoryDao
) {
    suspend fun selectPhrase(
        intervalSeconds: Long,
        todayScreenOnCount: Int,
        nowMillis: Long = System.currentTimeMillis()
    ): SelectedPhrase {
        val category = selectCategory(intervalSeconds, todayScreenOnCount, nowMillis)
        val recentPhraseIds = phraseHistoryDao.getRecentPhraseIds(limit = 3).toSet()
        val candidates = PhraseLibrary.phrasesFor(category)
        val phrase = candidates.firstOrNull { it.id !in recentPhraseIds }
            ?: candidates.first()
        val message = phrase.template.replace(
            "{interval}",
            PhraseLibrary.formatIntervalSeconds(intervalSeconds)
        )

        return SelectedPhrase(
            phrase = phrase,
            message = message
        )
    }

    suspend fun saveSelectedPhrase(
        phraseId: Int,
        screenOnEventId: Long?,
        shownAt: Long = System.currentTimeMillis()
    ) {
        phraseHistoryDao.insertPhraseHistory(
            PhraseHistoryEntity(
                phraseId = phraseId,
                screenOnEventId = screenOnEventId,
                shownAt = shownAt
            )
        )
    }

    private fun selectCategory(
        intervalSeconds: Long,
        todayScreenOnCount: Int,
        nowMillis: Long
    ): PhraseCategory {
        val hour = Instant.ofEpochMilli(nowMillis)
            .atZone(ZoneId.systemDefault())
            .hour

        return when {
            hour >= 22 || hour < 6 -> PhraseCategory.Night
            todayScreenOnCount >= 15 -> PhraseCategory.FrequentDay
            intervalSeconds <= 300 -> PhraseCategory.VeryShort
            intervalSeconds <= 600 -> PhraseCategory.ShortInterval
            else -> PhraseCategory.Focus
        }
    }
}

data class SelectedPhrase(
    val phrase: LocalPhrase,
    val message: String
)

package com.example.ttokyutne.ui.home

import com.example.ttokyutne.ui.settings.SettingsUiState
import java.time.DayOfWeek
import java.time.LocalDate

data class HomeUiState(
    val todayScreenOnCount: Int = 0,
    val lastIntervalSeconds: Long? = null,
    val isSavingTestEvent: Boolean = false,
    val todayAnalysis: TodayAnalysisUiState = TodayAnalysisUiState(),
    val weeklyAnalysis: WeeklyAnalysisUiState = WeeklyAnalysisUiState(),
    val settings: SettingsUiState = SettingsUiState()
)

data class TodayAnalysisUiState(
    val totalScreenOnCount: Int = 0,
    val averageIntervalSeconds: Long? = null,
    val shortestIntervalSeconds: Long? = null,
    val recheckWithinTenMinutesCount: Int = 0,
    val recentRecords: List<RecentScreenOnRecordUiState> = emptyList()
)

data class RecentScreenOnRecordUiState(
    val id: Long,
    val screenOnTime: Long,
    val intervalSeconds: Long?
)

data class WeeklyAnalysisUiState(
    val totalScreenOnCount: Int = 0,
    val dailyScreenOnCounts: List<DailyScreenOnCountUiState> = emptyList(),
    val dayOfWeekPatterns: List<DayOfWeekScreenOnPatternUiState> = emptyList(),
    val busiestHour: BusiestHourUiState? = null,
    val averageIntervalSeconds: Long? = null,
    val recheckWithinOneMinuteCount: Int = 0,
    val recheckWithinTenMinutesCount: Int = 0
)

data class DailyScreenOnCountUiState(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val count: Int
)

data class DayOfWeekScreenOnPatternUiState(
    val dayOfWeek: DayOfWeek,
    val count: Int
)

data class BusiestHourUiState(
    val hour: Int,
    val count: Int
)

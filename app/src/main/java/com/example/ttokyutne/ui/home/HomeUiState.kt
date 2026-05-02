package com.example.ttokyutne.ui.home

import com.example.ttokyutne.ui.settings.SettingsUiState
import java.time.DayOfWeek
import java.time.LocalDate

data class HomeUiState(
    val lastRecheckIntervalText: String = "42초",
    val todayScreenOnCount: Int = 27,
    val shortRecheckCount: Int = 8,
    val shortestRecheckIntervalText: String = "12초",
    val diffFromYesterday: Int = 4,
    val lastIntervalSeconds: Long? = null,
    val isSettingsLoaded: Boolean = false,
    val todayAnalysis: TodayAnalysisUiState = TodayAnalysisUiState(),
    val weeklyAnalysis: WeeklyAnalysisUiState = WeeklyAnalysisUiState(),
    val settings: SettingsUiState = SettingsUiState()
)

data class TodayAnalysisUiState(
    val totalScreenOnCount: Int = 0,
    val averageIntervalSeconds: Long? = null,
    val shortestIntervalSeconds: Long? = null,
    val recheckWithinTenMinutesCount: Int = 0,
    val hourlyScreenOnCounts: List<HourlyScreenOnCountUiState> = emptyList(),
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
    val count: Int,
    val hourlyCounts: List<HourlyScreenOnCountUiState> = emptyList()
)

data class DayOfWeekScreenOnPatternUiState(
    val dayOfWeek: DayOfWeek,
    val count: Int
)

data class HourlyScreenOnCountUiState(
    val hour: Int,
    val count: Int
)

data class BusiestHourUiState(
    val hour: Int,
    val count: Int
)

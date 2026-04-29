package com.example.ttokyutne.ui.home

data class HomeUiState(
    val todayScreenOnCount: Int = 0,
    val lastIntervalSeconds: Long? = null,
    val isSavingTestEvent: Boolean = false,
    val todayAnalysis: TodayAnalysisUiState = TodayAnalysisUiState()
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

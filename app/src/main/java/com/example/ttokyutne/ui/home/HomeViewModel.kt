package com.example.ttokyutne.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ttokyutne.data.local.AppDatabase
import com.example.ttokyutne.data.local.UserSettingsEntity
import com.example.ttokyutne.data.repository.ScreenOnEventRepository
import com.example.ttokyutne.data.repository.SettingsRepository
import com.example.ttokyutne.settings.RecheckAlertMode
import com.example.ttokyutne.ui.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToLong

private const val LOG_TAG = "Ttokyeonne"

class HomeViewModel(
    private val screenOnEventRepository: ScreenOnEventRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var selectedWeekAnchorDate: LocalDate? = null

    init {
        refreshTodayStats()
        refreshWeeklyStats()
        refreshSettings()
    }

    fun refreshTodayStats() {
        viewModelScope.launch {
            loadTodayStats()
        }
    }

    fun refreshWeeklyStats() {
        viewModelScope.launch {
            loadWeeklyStats(anchorDate = selectedWeekAnchorDate)
        }
    }

    fun refreshCurrentWeekStats() {
        selectedWeekAnchorDate = null
        refreshWeeklyStats()
    }

    fun showPreviousWeekStats() {
        viewModelScope.launch {
            selectedWeekAnchorDate = (selectedWeekAnchorDate ?: currentLocalDate()).minusWeeks(1)
            loadWeeklyStats(anchorDate = selectedWeekAnchorDate)
        }
    }

    fun showNextWeekStats() {
        viewModelScope.launch {
            val today = currentLocalDate()
            val nextAnchorDate = (selectedWeekAnchorDate ?: today).plusWeeks(1)
            selectedWeekAnchorDate = if (nextAnchorDate.isAfter(today)) today else nextAnchorDate
            loadWeeklyStats(anchorDate = selectedWeekAnchorDate)
        }
    }

    fun refreshSettings() {
        viewModelScope.launch {
            loadSettings()
        }
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val settings = settingsRepository.updateNotificationEnabled(enabled)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(LOG_TAG, "Updated notificationEnabled=$enabled")
        }
    }

    fun updateRecheckAlertMode(mode: RecheckAlertMode) {
        viewModelScope.launch {
            val settings = settingsRepository.updateRecheckAlertMode(mode)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(LOG_TAG, "Updated recheckAlertMode=${mode.storageValue}")
        }
    }

    fun updateMinIntervalSeconds(seconds: Long) {
        viewModelScope.launch {
            val settings = settingsRepository.updateMinIntervalSeconds(seconds)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(LOG_TAG, "Updated minIntervalSeconds=$seconds")
        }
    }

    fun updateVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val settings = settingsRepository.updateVibrationEnabled(enabled)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(LOG_TAG, "Updated vibrationEnabled=$enabled")
        }
    }

    fun updateMonitoringEnabled(enabled: Boolean) {
        viewModelScope.launch {
            val settings = settingsRepository.updateMonitoringEnabled(enabled)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(LOG_TAG, "Updated monitoringEnabled=$enabled")
        }
    }

    fun completeOnboarding(monitoringEnabled: Boolean) {
        viewModelScope.launch {
            val settings = settingsRepository.completeOnboarding(monitoringEnabled)
            _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
            Log.d(
                LOG_TAG,
                "Updated onboardingCompleted=true, monitoringEnabled=$monitoringEnabled"
            )
        }
    }

    fun deleteAllAppData() {
        viewModelScope.launch {
            settingsRepository.deleteAllAppData()
            loadTodayStats()
            loadWeeklyStats()
            Log.d(LOG_TAG, "Deleted screen_on_event and phrase_history data")
        }
    }

    private suspend fun loadTodayStats() {
        val nowMillis = System.currentTimeMillis()
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.ofInstant(Instant.ofEpochMilli(nowMillis), zoneId)
        val todayStartMillis = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val yesterdayStartMillis = today.minusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val todayEvents = screenOnEventRepository.getTodayEvents()
        val yesterdayEvents = screenOnEventRepository.getEventsBetween(
            startMillis = yesterdayStartMillis,
            endMillis = todayStartMillis
        )
        val lastEvent = screenOnEventRepository.getLastEvent()
        val intervalEvents = todayEvents.mapNotNull { it.intervalSeconds }
        val eventsByHour = todayEvents
            .groupingBy { event ->
                Instant.ofEpochMilli(event.screenOnTime).atZone(zoneId).hour
            }
            .eachCount()
        val shorterThanYesterdayCount = (yesterdayEvents.size - todayEvents.size).coerceAtLeast(0)
        val todayAnalysis = TodayAnalysisUiState(
            totalScreenOnCount = todayEvents.size,
            averageIntervalSeconds = intervalEvents.takeIf { it.isNotEmpty() }
                ?.average()
                ?.roundToLong(),
            shortestIntervalSeconds = intervalEvents.minOrNull(),
            recheckWithinTenMinutesCount = intervalEvents.count { it <= 600L },
            hourlyScreenOnCounts = (0..23).map { hour ->
                HourlyScreenOnCountUiState(
                    hour = hour,
                    count = eventsByHour[hour] ?: 0
                )
            },
            recentRecords = todayEvents
                .asReversed()
                .take(5)
                .map { event ->
                    RecentScreenOnRecordUiState(
                        id = event.id,
                        screenOnTime = event.screenOnTime,
                        intervalSeconds = event.intervalSeconds
                    )
                }
        )

        _uiState.update {
            it.copy(
                lastRecheckIntervalText = lastEvent?.intervalSeconds?.let(::formatHomeIntervalSeconds)
                    ?: "0초",
                todayScreenOnCount = todayEvents.size,
                shortRecheckCount = intervalEvents.count { intervalSeconds -> intervalSeconds <= 60L },
                shortestRecheckIntervalText = intervalEvents.minOrNull()
                    ?.let(::formatHomeIntervalSeconds)
                    ?: "0초",
                diffFromYesterday = shorterThanYesterdayCount,
                lastIntervalSeconds = lastEvent?.intervalSeconds,
                todayAnalysis = todayAnalysis
            )
        }

        Log.d(LOG_TAG, "Loaded today screen_on_event count=${todayEvents.size}")
    }

    private suspend fun loadWeeklyStats(
        anchorDate: LocalDate? = null,
        nowMillis: Long = System.currentTimeMillis()
    ) {
        val zoneId = ZoneId.systemDefault()
        val today = LocalDate.ofInstant(Instant.ofEpochMilli(nowMillis), zoneId)
        val weekAnchorDate = anchorDate ?: today
        val startDate = weekAnchorDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val endDate = startDate.plusDays(6)
        val weekDates = (0L..6L).map { dayOffset -> startDate.plusDays(dayOffset) }
        val startMillis = startDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endMillis = endDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        val weeklyEvents = screenOnEventRepository.getEventsBetween(startMillis, endMillis)
        val eventsByDate = weeklyEvents
            .groupingBy { event ->
                Instant.ofEpochMilli(event.screenOnTime).atZone(zoneId).toLocalDate()
            }
            .eachCount()
        val eventsByDateAndHour = weeklyEvents
            .groupBy { event ->
                Instant.ofEpochMilli(event.screenOnTime).atZone(zoneId).toLocalDate()
            }
            .mapValues { (_, events) ->
                events.groupingBy { event ->
                    Instant.ofEpochMilli(event.screenOnTime).atZone(zoneId).hour
                }.eachCount()
            }
        val dailyScreenOnCounts = weekDates.map { date ->
            DailyScreenOnCountUiState(
                date = date,
                dayOfWeek = date.dayOfWeek,
                count = eventsByDate[date] ?: 0,
                hourlyCounts = (0..23).map { hour ->
                    HourlyScreenOnCountUiState(
                        hour = hour,
                        count = eventsByDateAndHour[date]?.get(hour) ?: 0
                    )
                }
            )
        }
        val dayOfWeekPatterns = dailyScreenOnCounts.map { dailyCount ->
            DayOfWeekScreenOnPatternUiState(
                dayOfWeek = dailyCount.dayOfWeek,
                count = dailyCount.count
            )
        }
        val busiestHour = weeklyEvents
            .groupingBy { event ->
                Instant.ofEpochMilli(event.screenOnTime).atZone(zoneId).hour
            }
            .eachCount()
            .entries
            .sortedWith(compareByDescending<Map.Entry<Int, Int>> { it.value }.thenBy { it.key })
            .firstOrNull()
            ?.let { hourCount ->
                BusiestHourUiState(
                    hour = hourCount.key,
                    count = hourCount.value
                )
            }
        val intervalEvents = weeklyEvents.mapNotNull { it.intervalSeconds }
        val weeklyAnalysis = WeeklyAnalysisUiState(
            totalScreenOnCount = weeklyEvents.size,
            dailyScreenOnCounts = dailyScreenOnCounts,
            dayOfWeekPatterns = dayOfWeekPatterns,
            busiestHour = busiestHour,
            averageIntervalSeconds = intervalEvents.takeIf { it.isNotEmpty() }
                ?.average()
                ?.roundToLong(),
            recheckWithinOneMinuteCount = intervalEvents.count { it <= 60L },
            recheckWithinTenMinutesCount = intervalEvents.count { it <= 600L }
        )

        _uiState.update { it.copy(weeklyAnalysis = weeklyAnalysis) }

        Log.d(
            LOG_TAG,
            "Loaded weekly screen_on_event count=${weeklyEvents.size}, startDate=$startDate, endDate=$endDate"
        )
    }

    private fun currentLocalDate(nowMillis: Long = System.currentTimeMillis()): LocalDate {
        return LocalDate.ofInstant(
            Instant.ofEpochMilli(nowMillis),
            ZoneId.systemDefault()
        )
    }

    private suspend fun loadSettings() {
        val settings = settingsRepository.getSettings()
        _uiState.update { it.copy(settings = settings.toUiState(), isSettingsLoaded = true) }
        Log.d(
            LOG_TAG,
            "Loaded settings notificationEnabled=${settings.notificationEnabled}, minIntervalSeconds=${settings.minIntervalSeconds}, vibrationEnabled=${settings.vibrationEnabled}, recheckAlertMode=${settings.recheckAlertMode}"
        )
    }

    private fun UserSettingsEntity.toUiState(): SettingsUiState {
        return SettingsUiState(
            notificationEnabled = notificationEnabled,
            minIntervalSeconds = minIntervalSeconds,
            vibrationEnabled = vibrationEnabled,
            recheckAlertMode = RecheckAlertMode.fromStorageValue(recheckAlertMode),
            onboardingCompleted = onboardingCompleted,
            monitoringEnabled = monitoringEnabled,
            quietHoursEnabled = quietHoursEnabled,
            dataRetentionDays = dataRetentionDays
        )
    }

    class Factory(
        private val applicationContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                val database = AppDatabase.getInstance(applicationContext)
                val screenOnEventRepository = ScreenOnEventRepository(database.screenOnEventDao())
                val settingsRepository = SettingsRepository(
                    userSettingsDao = database.userSettingsDao(),
                    screenOnEventDao = database.screenOnEventDao(),
                    phraseHistoryDao = database.phraseHistoryDao()
                )
                return HomeViewModel(screenOnEventRepository, settingsRepository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

private fun formatHomeIntervalSeconds(intervalSeconds: Long): String {
    return when {
        intervalSeconds < 60 -> "${intervalSeconds}초"
        else -> {
            val minutes = intervalSeconds / 60
            val seconds = intervalSeconds % 60
            if (seconds == 0L) "${minutes}분" else "${minutes}분 ${seconds}초"
        }
    }
}

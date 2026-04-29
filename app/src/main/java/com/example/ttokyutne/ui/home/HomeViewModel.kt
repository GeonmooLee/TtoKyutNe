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
import com.example.ttokyutne.ui.settings.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToLong

private const val LOG_TAG = "Ttokyeonne"

class HomeViewModel(
    private val screenOnEventRepository: ScreenOnEventRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshTodayStats()
        refreshSettings()
    }

    fun recordTestEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTestEvent = true) }

            val recordedEvent = screenOnEventRepository.recordTestEvent()
            loadTodayStats()

            _uiState.update { it.copy(isSavingTestEvent = false) }
            Log.d(
                LOG_TAG,
                "Inserted test screen_on_event id=${recordedEvent.id}, intervalSeconds=${recordedEvent.intervalSeconds}"
            )
        }
    }

    fun refreshTodayStats() {
        viewModelScope.launch {
            loadTodayStats()
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
            _uiState.update { it.copy(settings = settings.toUiState()) }
            Log.d(LOG_TAG, "Updated notificationEnabled=$enabled")
        }
    }

    fun updateMinIntervalSeconds(seconds: Long) {
        viewModelScope.launch {
            val settings = settingsRepository.updateMinIntervalSeconds(seconds)
            _uiState.update { it.copy(settings = settings.toUiState()) }
            Log.d(LOG_TAG, "Updated minIntervalSeconds=$seconds")
        }
    }

    fun deleteAllAppData() {
        viewModelScope.launch {
            settingsRepository.deleteAllAppData()
            loadTodayStats()
            Log.d(LOG_TAG, "Deleted screen_on_event and phrase_history data")
        }
    }

    private suspend fun loadTodayStats() {
        val todayEvents = screenOnEventRepository.getTodayEvents()
        val lastEvent = screenOnEventRepository.getLastEvent()
        val intervalEvents = todayEvents.mapNotNull { it.intervalSeconds }
        val todayAnalysis = TodayAnalysisUiState(
            totalScreenOnCount = todayEvents.size,
            averageIntervalSeconds = intervalEvents.takeIf { it.isNotEmpty() }
                ?.average()
                ?.roundToLong(),
            shortestIntervalSeconds = intervalEvents.minOrNull(),
            recheckWithinTenMinutesCount = intervalEvents.count { it <= 600L },
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
                todayScreenOnCount = todayEvents.size,
                lastIntervalSeconds = lastEvent?.intervalSeconds,
                todayAnalysis = todayAnalysis
            )
        }

        Log.d(LOG_TAG, "Loaded today screen_on_event count=${todayEvents.size}")
    }

    private suspend fun loadSettings() {
        val settings = settingsRepository.getSettings()
        _uiState.update { it.copy(settings = settings.toUiState()) }
        Log.d(
            LOG_TAG,
            "Loaded settings notificationEnabled=${settings.notificationEnabled}, minIntervalSeconds=${settings.minIntervalSeconds}"
        )
    }

    private fun UserSettingsEntity.toUiState(): SettingsUiState {
        return SettingsUiState(
            notificationEnabled = notificationEnabled,
            minIntervalSeconds = minIntervalSeconds,
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

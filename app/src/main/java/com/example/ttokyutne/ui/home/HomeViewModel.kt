package com.example.ttokyutne.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ttokyutne.data.local.AppDatabase
import com.example.ttokyutne.data.repository.ScreenOnEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val LOG_TAG = "TtokDb"

class HomeViewModel(
    private val screenOnEventRepository: ScreenOnEventRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshTodayStats()
    }

    fun recordTestEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavingTestEvent = true) }

            val insertedId = screenOnEventRepository.recordTestEvent()
            loadTodayStats()

            _uiState.update { it.copy(isSavingTestEvent = false) }
            Log.d(LOG_TAG, "Inserted test screen_on_event id=$insertedId")
        }
    }

    private fun refreshTodayStats() {
        viewModelScope.launch {
            loadTodayStats()
        }
    }

    private suspend fun loadTodayStats() {
        val todayEvents = screenOnEventRepository.getTodayEvents()
        val lastEvent = screenOnEventRepository.getLastEvent()

        _uiState.update {
            it.copy(
                todayScreenOnCount = todayEvents.size,
                lastIntervalSeconds = lastEvent?.intervalSeconds
            )
        }

        Log.d(LOG_TAG, "Loaded today screen_on_event count=${todayEvents.size}")
    }

    class Factory(
        private val applicationContext: Context
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                val database = AppDatabase.getInstance(applicationContext)
                val repository = ScreenOnEventRepository(database.screenOnEventDao())
                return HomeViewModel(repository) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

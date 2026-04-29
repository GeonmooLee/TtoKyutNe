package com.example.ttokyutne.ui.home

data class HomeUiState(
    val todayScreenOnCount: Int = 0,
    val lastIntervalSeconds: Long? = null,
    val isSavingTestEvent: Boolean = false
)

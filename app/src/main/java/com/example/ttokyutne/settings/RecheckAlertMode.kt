package com.example.ttokyutne.settings

enum class RecheckAlertMode(val storageValue: String) {
    WithPhrase("WITH_PHRASE"),
    Simple("SIMPLE"),
    Off("OFF");

    companion object {
        fun fromStorageValue(value: String): RecheckAlertMode {
            return entries.firstOrNull { it.storageValue == value } ?: WithPhrase
        }
    }
}

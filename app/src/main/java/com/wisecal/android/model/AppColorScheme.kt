package com.wisecal.android.model

enum class AppColorScheme(val label: String) {
    SYSTEM("시스템"),
    DARK("다크"),
    LIGHT("라이트");

    companion object {
        fun fromString(value: String): AppColorScheme =
            entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

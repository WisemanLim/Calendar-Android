package com.wisecal.android.model

enum class TemplateCategory(val title: String) {
    MEDICATION("복용"),
    APPOINTMENT("약속"),
    FIXED("고정");

    companion object {
        fun fromString(value: String): TemplateCategory =
            entries.firstOrNull { it.name == value } ?: MEDICATION
    }
}

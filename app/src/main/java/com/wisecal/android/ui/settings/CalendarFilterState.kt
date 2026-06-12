package com.wisecal.android.ui.settings

import com.wisecal.android.data.CalendarInfo

/**
 * 캘린더 소스 선택 상태.
 * enabledIds 빈 Set = 전체 허용(AppSettings enabledCalendarIDs 기본값).
 * iOS CalendarSettingsView 대응.
 */
data class CalendarFilterState(
    val availableCalendars: List<CalendarInfo> = emptyList(),
    val enabledIds: Set<Long> = emptySet(),
) {
    val isAllEnabled: Boolean get() = enabledIds.isEmpty()

    val effectiveIds: Set<Long>
        get() = if (isAllEnabled) availableCalendars.map { it.id }.toSet() else enabledIds

    fun toggle(id: Long): CalendarFilterState = copy(
        enabledIds = if (id in enabledIds) enabledIds - id else enabledIds + id,
    )

    fun isEnabled(id: Long): Boolean = isAllEnabled || id in enabledIds
}

package com.wisecal.android.ui.copy

import com.wisecal.android.logic.PlannerLogic
import com.wisecal.android.model.CalendarEvent
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 이벤트 복사 BottomSheet 상태.
 * iOS EventCopySheet 대응. 소스 이벤트를 다른 날짜로 복사.
 */
data class EventCopyState(
    val sourceEvent: CalendarEvent,
    val targetDate: LocalDate,
    val includeNotes: Boolean = true,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = !isSaving

    val resolvedTitle: String
        get() = PlannerLogic.substituteTitlePatterns(sourceEvent.title, targetDate)

    val resolvedNotes: String?
        get() = if (includeNotes) sourceEvent.notes else null

    val resolvedStartTime: LocalDateTime
        get() = targetDate.atTime(sourceEvent.startTime.toLocalTime())

    val resolvedEndTime: LocalDateTime
        get() = targetDate.atTime(sourceEvent.endTime.toLocalTime())
}

package com.wisecal.android.logic

import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import java.time.LocalDateTime

/**
 * Todo 이벤트 기한 초과 판정.
 * PRD §3.4-B: isTodo && startDate < now && notes에 "- [ ]" 존재 → 빨간색 표시.
 */
object OverdueChecker {
    fun isOverdue(event: CalendarEvent, mode: PlannerMode, now: LocalDateTime): Boolean {
        if (mode != PlannerMode.TODO) return false
        if (event.isAllDay) return false
        if (event.startTime >= now) return false
        return event.notes?.contains("- [ ]") == true
    }
}

package com.wisecal.android.ui.copy

import com.wisecal.android.model.CalendarEvent
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class EventCopyStateTest {

    private fun event(
        title: String = "회의",
        notes: String? = "메모",
        startHour: Int = 9,
        endHour: Int = 10,
    ) = CalendarEvent(
        id = 1L, title = title, notes = notes,
        startTime = LocalDateTime.of(2026, 6, 1, startHour, 0),
        endTime   = LocalDateTime.of(2026, 6, 1, endHour, 0),
        isAllDay = false, location = null, calendarId = 1L,
    )

    private val base = LocalDate.of(2026, 6, 11)

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial includeNotes is true`() {
        assertTrue(EventCopyState(event(), base).includeNotes)
    }

    @Test fun `initial isSaving is false`() {
        assertFalse(EventCopyState(event(), base).isSaving)
    }

    @Test fun `initial canSave is true`() {
        assertTrue(EventCopyState(event(), base).canSave)
    }

    // ── canSave ────────────────────────────────────────────────────

    @Test fun `canSave false when isSaving`() {
        assertFalse(EventCopyState(event(), base, isSaving = true).canSave)
    }

    // ── resolvedNotes ──────────────────────────────────────────────

    @Test fun `resolvedNotes returns notes when includeNotes true`() {
        val s = EventCopyState(event(notes = "노트"), base, includeNotes = true)
        assertEquals("노트", s.resolvedNotes)
    }

    @Test fun `resolvedNotes null when includeNotes false`() {
        val s = EventCopyState(event(notes = "노트"), base, includeNotes = false)
        assertNull(s.resolvedNotes)
    }

    @Test fun `resolvedNotes null when sourceEvent notes is null`() {
        val s = EventCopyState(event(notes = null), base, includeNotes = true)
        assertNull(s.resolvedNotes)
    }

    // ── resolvedStartTime / resolvedEndTime ────────────────────────

    @Test fun `resolvedStartTime has targetDate and source time`() {
        val s = EventCopyState(event(startHour = 14), base)
        assertEquals(base, s.resolvedStartTime.toLocalDate())
        assertEquals(14, s.resolvedStartTime.hour)
    }

    @Test fun `resolvedEndTime has targetDate and source time`() {
        val s = EventCopyState(event(endHour = 15), base)
        assertEquals(base, s.resolvedEndTime.toLocalDate())
        assertEquals(15, s.resolvedEndTime.hour)
    }

    // ── resolvedTitle ──────────────────────────────────────────────

    @Test fun `resolvedTitle unchanged when no date pattern`() {
        val s = EventCopyState(event(title = "회의"), base)
        assertEquals("회의", s.resolvedTitle)
    }

    @Test fun `resolvedTitle substitutes yyyy-mm-dd pattern`() {
        val s = EventCopyState(event(title = "기록 yyyy-mm-dd"), LocalDate.of(2026, 6, 11))
        assertEquals("기록 2026-06-11", s.resolvedTitle)
    }

    // ── targetDate 변경 ────────────────────────────────────────────

    @Test fun `resolvedStartTime updates when targetDate changes`() {
        val s = EventCopyState(event(startHour = 9), base)
        val s2 = s.copy(targetDate = LocalDate.of(2026, 12, 25))
        assertEquals(LocalDate.of(2026, 12, 25), s2.resolvedStartTime.toLocalDate())
        assertEquals(9, s2.resolvedStartTime.hour)
    }
}

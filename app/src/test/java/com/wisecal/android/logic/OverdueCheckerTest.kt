package com.wisecal.android.logic

import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class OverdueCheckerTest {

    private val now = LocalDateTime.of(2026, 6, 11, 10, 0)

    private fun event(
        notes: String? = null,
        isAllDay: Boolean = false,
        startTime: LocalDateTime = now.minusHours(1),
    ) = CalendarEvent(
        id = 1L,
        title = "Test",
        notes = notes,
        startTime = startTime,
        endTime = startTime.plusHours(1),
        isAllDay = isAllDay,
        location = null,
        calendarId = 1L,
    )

    // ── DIARY 모드 ──────────────────────────────────────────────

    @Test
    fun `DIARY mode never overdue`() {
        val e = event(notes = "- [ ] task", startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.DIARY, now))
    }

    // ── PERSONAL_EVENT 모드 ────────────────────────────────────

    @Test
    fun `PERSONAL_EVENT mode never overdue`() {
        val e = event(notes = "- [ ] task", startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.PERSONAL_EVENT, now))
    }

    // ── TODO 모드 기본 조건 ────────────────────────────────────

    @Test
    fun `TODO past event with unchecked todo is overdue`() {
        val e = event(notes = "- [ ] do something", startTime = now.minusHours(1))
        assertTrue(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO past event with all checked todo is not overdue`() {
        val e = event(notes = "- [x] done", startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO past event with null notes is not overdue`() {
        val e = event(notes = null, startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO past event with empty notes is not overdue`() {
        val e = event(notes = "", startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    // ── 시간 경계 ──────────────────────────────────────────────

    @Test
    fun `TODO event starting exactly now is not overdue`() {
        val e = event(notes = "- [ ] task", startTime = now)
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO future event is not overdue`() {
        val e = event(notes = "- [ ] task", startTime = now.plusMinutes(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO event 1 second before now is overdue`() {
        val e = event(notes = "- [ ] task", startTime = now.minusSeconds(1))
        assertTrue(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    // ── 종일 이벤트 ────────────────────────────────────────────

    @Test
    fun `TODO all-day event is never overdue`() {
        val e = event(notes = "- [ ] task", isAllDay = true, startTime = now.minusHours(2))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    // ── 혼합 노트 ──────────────────────────────────────────────

    @Test
    fun `TODO mixed notes with at least one unchecked is overdue`() {
        val notes = "- [x] done\n- [ ] pending"
        val e = event(notes = notes, startTime = now.minusHours(1))
        assertTrue(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO notes with no todo markers is not overdue`() {
        val e = event(notes = "just a plain note", startTime = now.minusHours(1))
        assertFalse(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }

    @Test
    fun `TODO past event far in past with unchecked todo is overdue`() {
        val e = event(notes = "- [ ] old task", startTime = now.minusDays(30))
        assertTrue(OverdueChecker.isOverdue(e, PlannerMode.TODO, now))
    }
}

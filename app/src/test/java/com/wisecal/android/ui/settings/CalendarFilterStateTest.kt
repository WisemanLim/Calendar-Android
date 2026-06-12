package com.wisecal.android.ui.settings

import com.wisecal.android.data.CalendarInfo
import org.junit.Assert.*
import org.junit.Test

class CalendarFilterStateTest {

    private fun cal(id: Long) = CalendarInfo(id = id, name = "Cal$id", color = 0, accountName = "test")

    private val cal1 = cal(1L)
    private val cal2 = cal(2L)
    private val cal3 = cal(3L)

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial availableCalendars is empty`() {
        assertTrue(CalendarFilterState().availableCalendars.isEmpty())
    }

    @Test fun `initial enabledIds is empty`() {
        assertTrue(CalendarFilterState().enabledIds.isEmpty())
    }

    // ── isAllEnabled ───────────────────────────────────────────────

    @Test fun `isAllEnabled true when enabledIds empty`() {
        assertTrue(CalendarFilterState().isAllEnabled)
    }

    @Test fun `isAllEnabled false when enabledIds non-empty`() {
        assertFalse(CalendarFilterState(enabledIds = setOf(1L)).isAllEnabled)
    }

    // ── effectiveIds ───────────────────────────────────────────────

    @Test fun `effectiveIds returns all calendar IDs when isAllEnabled`() {
        val s = CalendarFilterState(availableCalendars = listOf(cal1, cal2), enabledIds = emptySet())
        assertEquals(setOf(1L, 2L), s.effectiveIds)
    }

    @Test fun `effectiveIds returns only enabledIds when not all enabled`() {
        val s = CalendarFilterState(availableCalendars = listOf(cal1, cal2, cal3), enabledIds = setOf(1L))
        assertEquals(setOf(1L), s.effectiveIds)
    }

    @Test fun `effectiveIds empty when isAllEnabled and no calendars`() {
        assertTrue(CalendarFilterState().effectiveIds.isEmpty())
    }

    // ── toggle ─────────────────────────────────────────────────────

    @Test fun `toggle adds id when not in set`() {
        val s = CalendarFilterState(availableCalendars = listOf(cal1)).toggle(1L)
        assertTrue(1L in s.enabledIds)
    }

    @Test fun `toggle removes id when in set`() {
        val s = CalendarFilterState(enabledIds = setOf(1L, 2L)).toggle(1L)
        assertFalse(1L in s.enabledIds)
        assertTrue(2L in s.enabledIds)
    }

    @Test fun `toggle preserves other ids`() {
        val s = CalendarFilterState(enabledIds = setOf(2L, 3L)).toggle(1L)
        assertEquals(setOf(1L, 2L, 3L), s.enabledIds)
    }

    // ── isEnabled ──────────────────────────────────────────────────

    @Test fun `isEnabled true when isAllEnabled`() {
        val s = CalendarFilterState(availableCalendars = listOf(cal1))
        assertTrue(s.isEnabled(1L))
    }

    @Test fun `isEnabled true when id in enabledIds`() {
        val s = CalendarFilterState(enabledIds = setOf(1L))
        assertTrue(s.isEnabled(1L))
    }

    @Test fun `isEnabled false when id not in enabledIds and not all enabled`() {
        val s = CalendarFilterState(enabledIds = setOf(2L))
        assertFalse(s.isEnabled(1L))
    }
}

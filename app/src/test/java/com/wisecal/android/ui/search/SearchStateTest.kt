package com.wisecal.android.ui.search

import com.wisecal.android.model.CalendarEvent
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class SearchStateTest {

    private fun event(id: Long = 1L, title: String = "이벤트") = CalendarEvent(
        id = id, title = title, notes = null,
        startTime = LocalDateTime.of(2026, 6, 11, 9, 0),
        endTime   = LocalDateTime.of(2026, 6, 11, 10, 0),
        isAllDay = false, location = null, calendarId = 1L,
    )

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial query is empty`() {
        assertEquals("", SearchState().query)
    }

    @Test fun `initial isSearching is false`() {
        assertFalse(SearchState().isSearching)
    }

    @Test fun `initial results is empty`() {
        assertTrue(SearchState().results.isEmpty())
    }

    // ── hasQuery ───────────────────────────────────────────────────

    @Test fun `hasQuery false when query empty`() {
        assertFalse(SearchState(query = "").hasQuery)
    }

    @Test fun `hasQuery false when query 1 char`() {
        assertFalse(SearchState(query = "a").hasQuery)
    }

    @Test fun `hasQuery false when query is only whitespace`() {
        assertFalse(SearchState(query = "  ").hasQuery)
    }

    @Test fun `hasQuery true when query 2 chars`() {
        assertTrue(SearchState(query = "ab").hasQuery)
    }

    @Test fun `hasQuery true when query longer`() {
        assertTrue(SearchState(query = "회의").hasQuery)
    }

    // ── displayResults ─────────────────────────────────────────────

    @Test fun `displayResults empty when no query`() {
        val s = SearchState(query = "", results = listOf(event()))
        assertTrue(s.displayResults.isEmpty())
    }

    @Test fun `displayResults empty when query too short`() {
        val s = SearchState(query = "가", results = listOf(event()))
        assertTrue(s.displayResults.isEmpty())
    }

    @Test fun `displayResults returns results when hasQuery`() {
        val events = listOf(event(1L), event(2L))
        val s = SearchState(query = "이벤트", results = events)
        assertEquals(2, s.displayResults.size)
    }

    // ── hasResults ─────────────────────────────────────────────────

    @Test fun `hasResults false when results empty`() {
        assertFalse(SearchState().hasResults)
    }

    @Test fun `hasResults true when results present`() {
        assertTrue(SearchState(results = listOf(event())).hasResults)
    }
}

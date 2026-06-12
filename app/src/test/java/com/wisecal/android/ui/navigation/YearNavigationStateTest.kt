package com.wisecal.android.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class YearNavigationStateTest {

    private val date2026 = LocalDate.of(2026, 6, 11)

    private fun state(
        year: Int = 2026,
        date: LocalDate = date2026,
        min: Int = 1970,
        max: Int = 2099,
    ) = YearNavigationState(year, date, min, max)

    // ── displayLabel ───────────────────────────────────────────

    @Test
    fun `displayLabel is year string`() {
        assertEquals("2026", state().displayLabel)
    }

    // ── 경계 조건 ──────────────────────────────────────────────

    @Test
    fun `canGoBack false at minYear`() {
        assertFalse(state(year = 1970, min = 1970).canGoBack)
    }

    @Test
    fun `canGoBack true above minYear`() {
        assertTrue(state(year = 1971, min = 1970).canGoBack)
    }

    @Test
    fun `canGoForward false at maxYear`() {
        assertFalse(state(year = 2099, max = 2099).canGoForward)
    }

    @Test
    fun `canGoForward true below maxYear`() {
        assertTrue(state(year = 2026, max = 2099).canGoForward)
    }

    // ── goBack / goForward ─────────────────────────────────────

    @Test
    fun `goBack decrements displayedYear`() {
        assertEquals(2025, state().goBack().displayedYear)
    }

    @Test
    fun `goBack updates selectedDate year`() {
        assertEquals(2025, state().goBack().selectedDate.year)
    }

    @Test
    fun `goBack preserves month and day`() {
        val result = state().goBack()
        assertEquals(6, result.selectedDate.monthValue)
        assertEquals(11, result.selectedDate.dayOfMonth)
    }

    @Test
    fun `goBack at minYear stays`() {
        val s = state(year = 1970, min = 1970)
        assertEquals(1970, s.goBack().displayedYear)
    }

    @Test
    fun `goForward increments displayedYear`() {
        assertEquals(2027, state().goForward().displayedYear)
    }

    @Test
    fun `goForward at maxYear stays`() {
        val s = state(year = 2099, max = 2099)
        assertEquals(2099, s.goForward().displayedYear)
    }

    // ── 윤년 경계 (2월 29일) ────────────────────────────────────

    @Test
    fun `goForward from Feb29 leap to non-leap adjusts to Feb28`() {
        // 2024 is leap year (Feb 29 exists), 2025 is not
        val leapDate = LocalDate.of(2024, 2, 29)
        val s = YearNavigationState(2024, leapDate, 1970, 2099)
        val result = s.goForward()
        assertEquals(2025, result.displayedYear)
        assertEquals(2, result.selectedDate.monthValue)
        assertEquals(28, result.selectedDate.dayOfMonth)
    }
}

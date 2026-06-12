package com.wisecal.android.ui.monthgrid

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class MonthGridStateTest {

    private val jun2026 = YearMonth.of(2026, 6)
    private val jul2026 = YearMonth.of(2026, 7)
    private val today = LocalDate.of(2026, 6, 11)

    private fun state(
        base: YearMonth = jun2026,
        selected: LocalDate = today,
        min: YearMonth = YearMonth.of(2021, 1),
        max: YearMonth = YearMonth.of(2031, 12),
    ) = MonthGridState(base, selected, min, max)

    // ── 기본 구조 ──────────────────────────────────────────────

    @Test
    fun `nextMonth is baseMonth plus one`() {
        assertEquals(jul2026, state().nextMonth)
    }

    @Test
    fun `December nextMonth is January of next year`() {
        val dec = MonthGridState(YearMonth.of(2025, 12), today, YearMonth.of(2020, 1), YearMonth.of(2030, 12))
        assertEquals(YearMonth.of(2026, 1), dec.nextMonth)
    }

    @Test
    fun `selectedDate preserved`() {
        assertEquals(today, state().selectedDate)
    }

    // ── 이동 경계 ──────────────────────────────────────────────

    @Test
    fun `canGoBack false at minMonth`() {
        assertFalse(state(base = YearMonth.of(2021, 1)).canGoBack)
    }

    @Test
    fun `canGoBack true above minMonth`() {
        assertTrue(state(base = YearMonth.of(2021, 2)).canGoBack)
    }

    @Test
    fun `canGoForward false when nextMonth equals maxMonth`() {
        // nextMonth = Nov 2031 → max = Nov 2031 → nextMonth < max is false
        val s = state(base = YearMonth.of(2031, 11), max = YearMonth.of(2031, 11))
        assertFalse(s.canGoForward)
    }

    @Test
    fun `canGoForward true when below maxMonth`() {
        assertTrue(state(base = YearMonth.of(2026, 6), max = YearMonth.of(2031, 12)).canGoForward)
    }

    // ── goBack / goForward ─────────────────────────────────────

    @Test
    fun `goBack decrements baseMonth`() {
        assertEquals(YearMonth.of(2026, 5), state().goBack().baseMonth)
    }

    @Test
    fun `goBack at minMonth stays`() {
        val s = state(base = YearMonth.of(2021, 1))
        assertEquals(YearMonth.of(2021, 1), s.goBack().baseMonth)
    }

    @Test
    fun `goForward increments baseMonth`() {
        assertEquals(jul2026, state().goForward().baseMonth)
    }

    @Test
    fun `goForward at maxMonth boundary stays`() {
        val s = state(base = YearMonth.of(2031, 11), max = YearMonth.of(2031, 11))
        assertEquals(YearMonth.of(2031, 11), s.goForward().baseMonth)
    }

    // ── selectDate ─────────────────────────────────────────────

    @Test
    fun `selectDate updates selectedDate`() {
        val newDate = LocalDate.of(2026, 7, 4)
        assertEquals(newDate, state().selectDate(newDate).selectedDate)
    }

    @Test
    fun `selectDate does not change baseMonth`() {
        val newDate = LocalDate.of(2026, 7, 4)
        assertEquals(jun2026, state().selectDate(newDate).baseMonth)
    }
}

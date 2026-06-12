package com.wisecal.android.ui.weekstrip

import com.wisecal.android.model.WeatherInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate

class WeekStripStateTest {

    // 2026-06-11 = Thursday
    private val thursday = LocalDate.of(2026, 6, 11)
    // Sunday of that week = 2026-06-07
    private val sunday = LocalDate.of(2026, 6, 7)

    // ── weekDays 구조 ──────────────────────────────────────────

    @Test
    fun `weekDays always 7 cells`() {
        assertEquals(7, WeekStripState(thursday).weekDays.size)
    }

    @Test
    fun `weekDays first cell is Sunday`() {
        val state = WeekStripState(thursday)
        assertEquals(DayOfWeek.SUNDAY, state.weekDays.first().dayOfWeek)
    }

    @Test
    fun `weekDays last cell is Saturday`() {
        val state = WeekStripState(thursday)
        assertEquals(DayOfWeek.SATURDAY, state.weekDays.last().dayOfWeek)
    }

    @Test
    fun `sundayStart is correct for Thursday selected`() {
        assertEquals(sunday, WeekStripState(thursday).sundayStart)
    }

    @Test
    fun `saturdayEnd is sunday plus 6`() {
        val state = WeekStripState(thursday)
        assertEquals(sunday.plusDays(6), state.saturdayEnd)
    }

    @Test
    fun `selected Sunday produces same weekDays as selected Thursday of that week`() {
        val fromSunday = WeekStripState(sunday)
        val fromThursday = WeekStripState(thursday)
        assertEquals(fromSunday.weekDays, fromThursday.weekDays)
    }

    @Test
    fun `weekDays contains selected date`() {
        val state = WeekStripState(thursday)
        assertTrue(state.weekDays.contains(thursday))
    }

    // ── 연도 경계 ──────────────────────────────────────────────

    @Test
    fun `year boundary Jan 1 Thursday week starts previous Dec Sunday`() {
        val jan1 = LocalDate.of(2026, 1, 1)  // Thursday
        val state = WeekStripState(jan1)
        assertEquals(DayOfWeek.SUNDAY, state.sundayStart.dayOfWeek)
        assertTrue(state.weekDays.contains(jan1))
    }

    // ── isSelected / isToday ───────────────────────────────────

    @Test
    fun `isSelected true for selectedDate`() {
        assertTrue(WeekStripState(thursday).isSelected(thursday))
    }

    @Test
    fun `isSelected false for other date`() {
        assertFalse(WeekStripState(thursday).isSelected(sunday))
    }

    @Test
    fun `isToday true for passed today`() {
        assertTrue(WeekStripState(thursday).isToday(thursday, thursday))
    }

    // ── 날씨 매핑 ──────────────────────────────────────────────

    @Test
    fun `weatherFor returns null when no map`() {
        assertNull(WeekStripState(thursday).weatherFor(thursday))
    }

    @Test
    fun `weatherFor returns mapped weather`() {
        val weather = WeatherInfo(22.0, 0)
        val state = WeekStripState(thursday, mapOf(thursday to weather))
        assertEquals(weather, state.weatherFor(thursday))
    }

    // ── fallbackEmoji ──────────────────────────────────────────

    @Test
    fun `fallbackEmoji 0 events returns happy`() {
        assertEquals("😀", WeekStripState(thursday).fallbackEmoji(0))
    }

    @Test
    fun `fallbackEmoji 4 events returns mindblown`() {
        assertEquals("🤯", WeekStripState(thursday).fallbackEmoji(4))
    }
}

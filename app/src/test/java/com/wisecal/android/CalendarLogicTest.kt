package com.wisecal.android

import com.wisecal.android.logic.CalendarLogic
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class CalendarLogicTest {

    @Test
    fun `dateKey formats correctly`() {
        assertEquals("2026-06-11", CalendarLogic.dateKey(LocalDate.of(2026, 6, 11)))
        assertEquals("2026-01-01", CalendarLogic.dateKey(LocalDate.of(2026, 1, 1)))
        assertEquals("2026-12-31", CalendarLogic.dateKey(LocalDate.of(2026, 12, 31)))
    }

    @Test
    fun `monthGrid always returns 42 cells`() {
        val months = listOf(
            YearMonth.of(2026, 1),
            YearMonth.of(2026, 2),
            YearMonth.of(2024, 2), // 윤년
            YearMonth.of(2026, 6),
        )
        for (ym in months) {
            val grid = CalendarLogic.monthGrid(ym)
            assertEquals("$ym should have 42 cells", 42, grid.size)
        }
    }

    @Test
    fun `monthGrid first cell is Sunday of the week containing the 1st`() {
        // 2026-06: June 1 = Monday → leading = 1 (Sunday before = May 31)
        val grid = CalendarLogic.monthGrid(YearMonth.of(2026, 6))
        assertEquals(LocalDate.of(2026, 5, 31), grid[0].date)
        assertEquals(false, grid[0].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 6, 1), grid[1].date)
        assertEquals(true, grid[1].isCurrentMonth)
    }

    @Test
    fun `monthGrid when month starts on Sunday has no leading cells`() {
        // Find a month starting on Sunday: 2026-11-01 = Sunday
        val grid = CalendarLogic.monthGrid(YearMonth.of(2026, 11))
        assertEquals(LocalDate.of(2026, 11, 1), grid[0].date)
        assertEquals(true, grid[0].isCurrentMonth)
    }

    @Test
    fun `monthGrid trailing cells are next month`() {
        val ym = YearMonth.of(2026, 6) // 30 days
        val grid = CalendarLogic.monthGrid(ym)
        val lastCurrentMonth = grid.last { it.isCurrentMonth }
        assertEquals(LocalDate.of(2026, 6, 30), lastCurrentMonth.date)
        val afterLast = grid.last()
        assertEquals(false, afterLast.isCurrentMonth)
    }

    @Test
    fun `monthGrid february leap year`() {
        val grid = CalendarLogic.monthGrid(YearMonth.of(2024, 2)) // 29 days
        assertEquals(42, grid.size)
        val currentMonthDays = grid.count { it.isCurrentMonth }
        assertEquals(29, currentMonthDays)
    }
}

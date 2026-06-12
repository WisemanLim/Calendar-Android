package com.wisecal.android.ui

import com.wisecal.android.logic.CalendarLogic
import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.model.WeatherInfo
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class CalendarUiStateTest {

    private fun event(date: LocalDate, id: Long = 1L) = CalendarEvent(
        id = id,
        title = "이벤트",
        notes = null,
        startTime = date.atTime(9, 0),
        endTime = date.atTime(10, 0),
        isAllDay = false,
        location = null,
        calendarId = 1L,
    )

    private fun stateWithEvents(
        selected: LocalDate,
        vararg pairs: Pair<LocalDate, Int>,
    ): CalendarUiState {
        val map = pairs.associate { (date, count) ->
            CalendarLogic.dateKey(date) to (1..count).map { idx -> event(date, idx.toLong()) }
        }
        return CalendarUiState(selectedDate = selected, eventsForMonth = map)
    }

    // ── 기본값 ────────────────────────────────────────────────────

    @Test fun `initial plannerMode is TODO`() {
        assertEquals(PlannerMode.TODO, CalendarUiState().plannerMode)
    }

    @Test fun `initial isLoading is false`() {
        assertFalse(CalendarUiState().isLoading)
    }

    @Test fun `initial weather is null`() {
        assertNull(CalendarUiState().weather)
    }

    // ── eventsForSelectedDate ────────────────────────────────────

    @Test fun `eventsForSelectedDate empty when no events`() {
        val state = CalendarUiState(selectedDate = LocalDate.of(2026, 6, 11))
        assertTrue(state.eventsForSelectedDate.isEmpty())
    }

    @Test fun `eventsForSelectedDate returns correct count`() {
        val date = LocalDate.of(2026, 6, 11)
        val state = stateWithEvents(date, date to 3)
        assertEquals(3, state.eventsForSelectedDate.size)
    }

    @Test fun `eventsForSelectedDate ignores other dates`() {
        val selected = LocalDate.of(2026, 6, 11)
        val other    = LocalDate.of(2026, 6, 12)
        val state = stateWithEvents(selected, other to 5)
        assertTrue(state.eventsForSelectedDate.isEmpty())
    }

    @Test fun `eventsForSelectedDate updates when selectedDate changes`() {
        val dateA = LocalDate.of(2026, 6, 11)
        val dateB = LocalDate.of(2026, 6, 15)
        val state = stateWithEvents(dateA, dateA to 2, dateB to 4)
        assertEquals(2, state.eventsForSelectedDate.size)
        assertEquals(4, state.copy(selectedDate = dateB).eventsForSelectedDate.size)
    }

    // ── monthGrid ─────────────────────────────────────────────────

    @Test fun `monthGrid has 42 cells`() {
        assertEquals(42, CalendarUiState().monthGrid.size)
    }

    @Test fun `monthGrid event count 0 for date with no events`() {
        val state = CalendarUiState(currentMonth = YearMonth.of(2026, 6))
        val day = state.monthGrid.first { it.isCurrentMonth && it.date.dayOfMonth == 11 }
        assertEquals(0, day.eventCount)
    }

    @Test fun `monthGrid event count matches eventsForMonth`() {
        val date = LocalDate.of(2026, 6, 11)
        val state = stateWithEvents(date, date to 5).copy(currentMonth = YearMonth.of(2026, 6))
        val day = state.monthGrid.first { it.date == date }
        assertEquals(5, day.eventCount)
    }

    @Test fun `monthGrid uses currentMonth not selectedDate`() {
        val state = CalendarUiState(
            selectedDate = LocalDate.of(2026, 7, 1),
            currentMonth = YearMonth.of(2026, 6),
        )
        val juneCell = state.monthGrid.filter { it.isCurrentMonth }
        assertTrue(juneCell.all { it.date.monthValue == 6 })
    }

    @Test fun `monthGrid leading cells are not current month`() {
        val state = CalendarUiState(currentMonth = YearMonth.of(2026, 6))
        val firstCurrentIdx = state.monthGrid.indexOfFirst { it.isCurrentMonth }
        state.monthGrid.subList(0, firstCurrentIdx).forEach {
            assertFalse(it.isCurrentMonth)
        }
    }

    @Test fun `monthGrid trailing cells are not current month`() {
        val state = CalendarUiState(currentMonth = YearMonth.of(2026, 6))
        val lastCurrentIdx = state.monthGrid.indexOfLast { it.isCurrentMonth }
        state.monthGrid.subList(lastCurrentIdx + 1, 42).forEach {
            assertFalse(it.isCurrentMonth)
        }
    }
}

package com.wisecal.android.data

import com.wisecal.android.model.CalendarEvent
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarEventTest {

    private fun event(
        title: String = "테스트",
        startTime: LocalDateTime = LocalDateTime.of(2026, 6, 11, 9, 0),
        endTime: LocalDateTime = LocalDateTime.of(2026, 6, 11, 10, 0),
        isAllDay: Boolean = false,
        notes: String? = null,
    ) = CalendarEvent(
        id = 1L,
        title = title,
        notes = notes,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        location = null,
        calendarId = 1L,
    )

    @Test
    fun `date returns startTime local date`() {
        val e = event(startTime = LocalDateTime.of(2026, 6, 11, 14, 30))
        assertEquals(LocalDate.of(2026, 6, 11), e.date)
    }

    @Test
    fun `displayTime returns HH-mm for timed event`() {
        val e = event(startTime = LocalDateTime.of(2026, 6, 11, 9, 5))
        assertEquals("09:05", e.displayTime)
    }

    @Test
    fun `displayTime returns empty string for all-day event`() {
        val e = event(isAllDay = true)
        assertEquals("", e.displayTime)
    }

    @Test
    fun `isEmpty true when title blank and notes null`() {
        val e = event(title = "", notes = null)
        assertTrue(e.isEmpty)
    }

    @Test
    fun `isEmpty false when title has content`() {
        val e = event(title = "회의")
        assertFalse(e.isEmpty)
    }

    @Test
    fun `isEmpty false when notes has content`() {
        val e = event(title = "", notes = "메모 있음")
        assertFalse(e.isEmpty)
    }

    @Test
    fun `midnight boundary displayTime`() {
        val e = event(startTime = LocalDateTime.of(2026, 6, 11, 0, 0))
        assertEquals("00:00", e.displayTime)
    }
}

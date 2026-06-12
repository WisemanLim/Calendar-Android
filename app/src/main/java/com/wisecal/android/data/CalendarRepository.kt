package com.wisecal.android.data

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.wisecal.android.model.CalendarEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

data class CalendarInfo(
    val id: Long,
    val name: String,
    val color: Int,
    val accountName: String,
)

interface CalendarRepository {
    suspend fun getCalendars(): List<CalendarInfo>
    suspend fun fetchEventsForMonth(yearMonth: YearMonth, calendarIds: Set<Long>): Map<String, List<CalendarEvent>>
    suspend fun fetchEventsForDate(date: LocalDate, calendarIds: Set<Long>): List<CalendarEvent>
    suspend fun searchEvents(query: String, from: LocalDate, to: LocalDate): List<CalendarEvent>
    suspend fun addEvent(
        title: String,
        notes: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        isAllDay: Boolean,
        location: String?,
        calendarId: Long,
        rrule: String? = null,
    ): Long?
    suspend fun updateEvent(
        eventId: Long,
        title: String,
        notes: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        isAllDay: Boolean,
        location: String?,
    ): Boolean
    suspend fun deleteEvent(eventId: Long): Boolean
}

class AndroidCalendarRepository(private val context: Context) : CalendarRepository {

    private val zone = ZoneId.systemDefault()

    private fun LocalDateTime.toEpochMs(): Long =
        atZone(zone).toInstant().toEpochMilli()

    private fun Long.toLocalDateTime(): LocalDateTime =
        LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(this), zone)

    override suspend fun getCalendars(): List<CalendarInfo> = withContext(Dispatchers.IO) {
        val projection = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.ACCOUNT_NAME,
        )
        val results = mutableListOf<CalendarInfo>()
        context.contentResolver
            .query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    results += CalendarInfo(
                        id          = cursor.getLong(0),
                        name        = cursor.getString(1) ?: "",
                        color       = cursor.getInt(2),
                        accountName = cursor.getString(3) ?: "",
                    )
                }
            }
        results
    }

    override suspend fun fetchEventsForMonth(
        yearMonth: YearMonth,
        calendarIds: Set<Long>,
    ): Map<String, List<CalendarEvent>> = withContext(Dispatchers.IO) {
        val start = yearMonth.atDay(1).atStartOfDay().toEpochMs()
        val end   = yearMonth.atEndOfMonth().plusDays(1).atStartOfDay().toEpochMs()
        val events = queryEvents(start, end, calendarIds)
        events.groupBy { it.date.toString() }
    }

    override suspend fun fetchEventsForDate(
        date: LocalDate,
        calendarIds: Set<Long>,
    ): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val start = date.atStartOfDay().toEpochMs()
        val end   = date.plusDays(1).atStartOfDay().toEpochMs()
        queryEvents(start, end, calendarIds)
    }

    override suspend fun searchEvents(
        query: String,
        from: LocalDate,
        to: LocalDate,
    ): List<CalendarEvent> = withContext(Dispatchers.IO) {
        val start = from.atStartOfDay().toEpochMs()
        val end   = to.plusDays(1).atStartOfDay().toEpochMs()
        queryEvents(start, end, emptySet())
            .filter { it.title.contains(query, ignoreCase = true) }
    }

    private fun queryEvents(
        startMs: Long,
        endMs: Long,
        calendarIds: Set<Long>,
    ): List<CalendarEvent> {
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.DISPLAY_COLOR,
        )
        val idFilter = if (calendarIds.isNotEmpty())
            " AND ${CalendarContract.Events.CALENDAR_ID} IN (${calendarIds.joinToString(",")})"
        else ""
        val selection = "(${CalendarContract.Events.DTSTART} < ? AND ${CalendarContract.Events.DTEND} > ?)$idFilter"
        val selArgs   = arrayOf(endMs.toString(), startMs.toString())

        val results = mutableListOf<CalendarEvent>()
        context.contentResolver
            .query(CalendarContract.Events.CONTENT_URI, projection, selection, selArgs, CalendarContract.Events.DTSTART)
            ?.use { cursor ->
                while (cursor.moveToNext()) {
                    val dtStart = cursor.getLong(3)
                    val dtEnd   = cursor.getLong(4)
                    results += CalendarEvent(
                        id            = cursor.getLong(0),
                        title         = cursor.getString(1) ?: "",
                        notes         = cursor.getString(2),
                        startTime     = dtStart.toLocalDateTime(),
                        endTime       = if (dtEnd > 0) dtEnd.toLocalDateTime() else dtStart.toLocalDateTime(),
                        isAllDay      = cursor.getInt(5) != 0,
                        location      = cursor.getString(6),
                        calendarId    = cursor.getLong(7),
                        calendarColor = cursor.getInt(8),
                    )
                }
            }
        return results
    }

    override suspend fun addEvent(
        title: String,
        notes: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        isAllDay: Boolean,
        location: String?,
        calendarId: Long,
        rrule: String?,
    ): Long? = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE,          title)
            put(CalendarContract.Events.DESCRIPTION,    notes)
            put(CalendarContract.Events.DTSTART,        startTime.toEpochMs())
            put(CalendarContract.Events.ALL_DAY,        if (isAllDay) 1 else 0)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.CALENDAR_ID,    calendarId)
            put(CalendarContract.Events.EVENT_TIMEZONE, zone.id)
            if (rrule != null) {
                // RRULE 이벤트는 DURATION 사용 (DTEND와 배타적)
                put(CalendarContract.Events.RRULE,    rrule)
                put(CalendarContract.Events.DURATION, "P${(endTime.toEpochMs() - startTime.toEpochMs()) / 60000}M")
            } else {
                put(CalendarContract.Events.DTEND, endTime.toEpochMs())
            }
        }
        context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            ?.lastPathSegment?.toLongOrNull()
    }

    override suspend fun updateEvent(
        eventId: Long,
        title: String,
        notes: String?,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        isAllDay: Boolean,
        location: String?,
    ): Boolean = withContext(Dispatchers.IO) {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        val values = ContentValues().apply {
            put(CalendarContract.Events.TITLE,          title)
            put(CalendarContract.Events.DESCRIPTION,    notes)
            put(CalendarContract.Events.DTSTART,        startTime.toEpochMs())
            put(CalendarContract.Events.DTEND,          endTime.toEpochMs())
            put(CalendarContract.Events.ALL_DAY,        if (isAllDay) 1 else 0)
            put(CalendarContract.Events.EVENT_LOCATION, location)
            put(CalendarContract.Events.EVENT_TIMEZONE, zone.id)
        }
        context.contentResolver.update(uri, values, null, null) > 0
    }

    override suspend fun deleteEvent(eventId: Long): Boolean = withContext(Dispatchers.IO) {
        val uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        context.contentResolver.delete(uri, null, null) > 0
    }
}

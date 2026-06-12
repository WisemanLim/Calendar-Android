package com.wisecal.android.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * CalendarProvider 단일 이벤트 레코드.
 * iOS EKEvent 대응. ContentResolver 조회 결과 매핑에 사용.
 */
data class CalendarEvent(
    val id: Long,
    val title: String,
    val notes: String?,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val isAllDay: Boolean,
    val location: String?,
    val calendarId: Long,
    val calendarColor: Int = 0,
) {
    val date: LocalDate get() = startTime.toLocalDate()

    /** 일정 없음 판별 — 제목 비어있고 메모 없음. */
    val isEmpty: Boolean get() = title.isBlank() && notes.isNullOrBlank()

    /** 목록 표시 시간 문자열 `HH:mm`. 종일=빈 문자열. */
    val displayTime: String
        get() = if (isAllDay) ""
        else startTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}

package com.wisecal.android.ui

import com.wisecal.android.logic.CalendarLogic
import com.wisecal.android.logic.DayInfo
import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.model.WeatherInfo
import java.time.LocalDate
import java.time.YearMonth

/**
 * 메인 캘린더 화면 전체 UI 상태.
 * ViewModel이 StateFlow로 방출, Compose가 구독.
 */
data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: YearMonth = YearMonth.now(),
    /** dateKey → 이벤트 목록 (current + next month 포함). */
    val eventsForMonth: Map<String, List<CalendarEvent>> = emptyMap(),
    val plannerMode: PlannerMode = PlannerMode.TODO,
    val enabledCalendarIds: Set<Long> = emptySet(),
    val weather: WeatherInfo? = null,
    /** 날짜별 날씨 (주간 예보 포함). */
    val weekWeatherMap: Map<LocalDate, WeatherInfo> = emptyMap(),
    val isLoading: Boolean = false,
) {
    /** 선택된 날짜의 이벤트 목록. */
    val eventsForSelectedDate: List<CalendarEvent>
        get() = eventsForMonth[CalendarLogic.dateKey(selectedDate)] ?: emptyList()

    /** 42칸 월 그리드 (이벤트 count 포함). */
    val monthGrid: List<DayInfo>
        get() = CalendarLogic.monthGrid(currentMonth).map { day ->
            val count = eventsForMonth[CalendarLogic.dateKey(day.date)]?.size ?: 0
            if (count != 0) day.copy(eventCount = count) else day
        }

    /** 다음 달 42칸 그리드 (eventsForMonth에 다음 달 데이터도 포함되어 있으므로 count 반영됨). */
    val nextMonthGrid: List<DayInfo>
        get() = CalendarLogic.monthGrid(currentMonth.plusMonths(1)).map { day ->
            val count = eventsForMonth[CalendarLogic.dateKey(day.date)]?.size ?: 0
            if (count != 0) day.copy(eventCount = count) else day
        }
}

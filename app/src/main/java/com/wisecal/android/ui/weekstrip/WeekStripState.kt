package com.wisecal.android.ui.weekstrip

import com.wisecal.android.model.WeatherInfo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * 주간 스트립 7칸 상태.
 * PRD §3.1: 선택일 포함 주(일~토) 7셀 + 날씨 매핑.
 */
data class WeekStripState(
    val selectedDate: LocalDate,
    val weatherMap: Map<LocalDate, WeatherInfo?> = emptyMap(),
) {
    /** 선택일 포함 주의 일요일 시작 7일 */
    val weekDays: List<LocalDate> = run {
        val sunday = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        (0L..6L).map { sunday.plusDays(it) }
    }

    val sundayStart: LocalDate get() = weekDays.first()
    val saturdayEnd: LocalDate get() = weekDays.last()

    fun weatherFor(date: LocalDate): WeatherInfo? = weatherMap[date]

    fun isSelected(date: LocalDate): Boolean = date == selectedDate

    fun isToday(date: LocalDate, today: LocalDate = LocalDate.now()): Boolean = date == today

    /** 날씨 실패(null) 시 이모지 — 주간 이벤트 수 기반. 0~1=😀, 2~3=😊, 4+=🤯 */
    fun fallbackEmoji(eventCount: Int): String = when {
        eventCount <= 1 -> "😀"
        eventCount <= 3 -> "😊"
        else -> "🤯"
    }
}

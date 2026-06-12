package com.wisecal.android.logic

import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

/**
 * 캘린더 날짜 로직 (순수 함수 — 뷰 비의존, 테스트 가능).
 * SUN-first(일요일 시작) 고정.
 * iOS CalendarLogic.swift 대응.
 */
object CalendarLogic {

    private val keyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /** 이벤트 캐시 키 (yyyy-MM-dd). */
    fun dateKey(date: LocalDate): String = date.format(keyFormatter)

    /**
     * 한 달의 6주(42칸) 그리드 생성.
     * SUN-first: 선행 빈칸 = 그 달 1일의 요일(SUN=0, MON=1, … SAT=6).
     * 앞/뒤는 전·다음 달 날짜로 채워 항상 42칸.
     */
    fun monthGrid(yearMonth: YearMonth): List<DayInfo> {
        val result = mutableListOf<DayInfo>()
        val firstDay = yearMonth.atDay(1)
        // DayOfWeek: MONDAY=1 … SUNDAY=7  →  SUN-first index: SUN=0
        val leadingCount = (firstDay.dayOfWeek.value % 7) // MON=1→1, …, SUN=7→0
        val daysInMonth = yearMonth.lengthOfMonth()

        // 전월 선행일
        for (i in leadingCount - 1 downTo 0) {
            result.add(DayInfo(date = firstDay.minusDays(i.toLong() + 1), isCurrentMonth = false))
        }
        // 당월
        for (day in 1..daysInMonth) {
            result.add(DayInfo(date = yearMonth.atDay(day), isCurrentMonth = true))
        }
        // 후행일 (42칸)
        val trailing = 42 - result.size
        val lastDay = yearMonth.atEndOfMonth()
        for (i in 1..trailing) {
            result.add(DayInfo(date = lastDay.plusDays(i.toLong()), isCurrentMonth = false))
        }
        return result
    }
}

data class DayInfo(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val eventCount: Int = 0,
)

package com.wisecal.android.ui.monthgrid

import java.time.LocalDate
import java.time.YearMonth

/**
 * 하단 미니 달력 상태 — 현재 월 + 다음 월 나란히 표시.
 * PRD §3.1: 이동 경계(minMonth/maxMonth) + 스와이프 좌=다음달 / 우=이전달.
 */
data class MonthGridState(
    val baseMonth: YearMonth,
    val selectedDate: LocalDate,
    val minMonth: YearMonth = YearMonth.now().minusYears(5),
    val maxMonth: YearMonth = YearMonth.now().plusYears(5),
) {
    /** 두 번째 미니 달력 */
    val nextMonth: YearMonth = baseMonth.plusMonths(1)

    val canGoBack: Boolean = baseMonth > minMonth
    val canGoForward: Boolean = nextMonth < maxMonth

    fun goBack(): MonthGridState = if (canGoBack) copy(baseMonth = baseMonth.minusMonths(1)) else this
    fun goForward(): MonthGridState = if (canGoForward) copy(baseMonth = baseMonth.plusMonths(1)) else this

    fun selectDate(date: LocalDate): MonthGridState = copy(selectedDate = date)
}

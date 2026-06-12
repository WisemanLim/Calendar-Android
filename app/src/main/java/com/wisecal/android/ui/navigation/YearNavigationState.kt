package com.wisecal.android.ui.navigation

import java.time.LocalDate

/**
 * 유틸리티 바 `< 연도 >` 연도 이동 상태.
 * PRD §3.1: 년 이동 버튼. 이동 시 해당 연도의 같은 월/일로 selectedDate 이동.
 */
data class YearNavigationState(
    val displayedYear: Int,
    val selectedDate: LocalDate,
    val minYear: Int = 1970,
    val maxYear: Int = 2099,
) {
    val canGoBack: Boolean = displayedYear > minYear
    val canGoForward: Boolean = displayedYear < maxYear

    val displayLabel: String get() = "$displayedYear"

    fun goBack(): YearNavigationState =
        if (canGoBack) adjustedYear(displayedYear - 1) else this

    fun goForward(): YearNavigationState =
        if (canGoForward) adjustedYear(displayedYear + 1) else this

    private fun adjustedYear(year: Int): YearNavigationState {
        val newDate = try {
            selectedDate.withYear(year)
        } catch (_: Exception) {
            // 2월 29일 같은 경우 말일로 조정
            LocalDate.of(year, selectedDate.monthValue, 1)
                .withDayOfMonth(
                    minOf(selectedDate.dayOfMonth, LocalDate.of(year, selectedDate.monthValue, 1).lengthOfMonth())
                )
        }
        return copy(displayedYear = year, selectedDate = newDate)
    }
}

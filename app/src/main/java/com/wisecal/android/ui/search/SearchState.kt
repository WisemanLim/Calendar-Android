package com.wisecal.android.ui.search

import com.wisecal.android.model.CalendarEvent

/**
 * 이벤트 검색 화면 상태 (순수 data class).
 * 검색어 2글자 이상부터 유효.
 */
data class SearchState(
    val query: String = "",
    val results: List<CalendarEvent> = emptyList(),
    val isSearching: Boolean = false,
) {
    /** 유효 검색어: 공백 제거 후 2자 이상. */
    val hasQuery: Boolean
        get() = query.trim().length >= 2

    val hasResults: Boolean
        get() = results.isNotEmpty()

    /**
     * 화면에 표시할 결과 목록.
     * hasQuery false이면 빈 리스트 (검색 전 화면).
     */
    val displayResults: List<CalendarEvent>
        get() = if (hasQuery) results else emptyList()
}

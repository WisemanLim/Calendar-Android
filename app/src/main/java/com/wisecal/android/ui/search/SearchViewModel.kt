package com.wisecal.android.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisecal.android.data.CalendarRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class SearchViewModel(
    private val calendarRepo: CalendarRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query, isSearching = query.trim().length >= 2) }
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _state.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch(ioDispatcher) {
            delay(300) // debounce
            val from = LocalDate.now().minusYears(2)
            val to   = LocalDate.now().plusYears(1)
            val results = runCatching {
                calendarRepo.searchEvents(query.trim(), from, to)
            }.getOrElse { emptyList() }
            _state.update { it.copy(results = results, isSearching = false) }
        }
    }

    fun clearQuery() {
        searchJob?.cancel()
        _state.value = SearchState()
    }
}

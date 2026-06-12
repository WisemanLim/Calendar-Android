package com.wisecal.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisecal.android.data.AppSettings
import com.wisecal.android.data.CalendarRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val calendarRepo: CalendarRepository,
    private val appSettings: AppSettings,
) : ViewModel() {

    private val _filterState = MutableStateFlow(CalendarFilterState())
    val filterState: StateFlow<CalendarFilterState> = _filterState.asStateFlow()

    init {
        viewModelScope.launch {
            val calendars = calendarRepo.getCalendars()
            val enabledIds = appSettings.enabledCalendarIds.first()
                .mapNotNull { it.toLongOrNull() }
                .toSet()
            _filterState.value = CalendarFilterState(
                availableCalendars = calendars,
                enabledIds         = enabledIds,
            )
        }
    }

    fun toggleCalendar(id: Long) {
        val next = _filterState.value.toggle(id)
        _filterState.value = next
        viewModelScope.launch {
            appSettings.setEnabledCalendarIds(
                next.enabledIds.map { it.toString() }.toSet()
            )
        }
    }
}

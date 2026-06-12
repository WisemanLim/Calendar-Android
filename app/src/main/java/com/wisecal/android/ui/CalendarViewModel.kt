package com.wisecal.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisecal.android.data.AppSettings
import com.wisecal.android.data.CalendarRepository
import com.wisecal.android.data.WeatherRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class CalendarViewModel(
    private val calendarRepo: CalendarRepository,
    private val appSettings: AppSettings,
    private val weatherRepo: WeatherRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        observeSettings()
        loadMonth(_uiState.value.currentMonth)
    }

    // ── 설정 관찰 ────────────────────────────────────────────────

    private fun observeSettings() {
        viewModelScope.launch {
            appSettings.plannerMode.collect { mode ->
                _uiState.update { it.copy(plannerMode = mode) }
            }
        }
        viewModelScope.launch {
            appSettings.enabledCalendarIds.collect { ids ->
                val longIds = ids.mapNotNull(String::toLongOrNull).toSet()
                _uiState.update { it.copy(enabledCalendarIds = longIds) }
                loadMonth(_uiState.value.currentMonth)
            }
        }
    }

    // ── 날짜/월 탐색 ──────────────────────────────────────────────

    fun selectDate(date: LocalDate) {
        val currentMonth = _uiState.value.currentMonth
        _uiState.update { it.copy(selectedDate = date) }
        if (YearMonth.from(date) != currentMonth) {
            navigateToMonth(YearMonth.from(date))
        }
    }

    fun navigateMonth(delta: Int) {
        val newMonth = _uiState.value.currentMonth.plusMonths(delta.toLong())
        navigateToMonth(newMonth)
    }

    fun refresh() {
        loadMonth(_uiState.value.currentMonth)
    }

    // ── 날씨 ─────────────────────────────────────────────────────

    fun loadWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch(ioDispatcher) {
            val currentAsync = async { weatherRepo.getWeather(latitude, longitude) }
            val weeklyAsync  = async { weatherRepo.getWeeklyForecast(latitude, longitude) }
            val current = currentAsync.await()
            val weekly  = weeklyAsync.await()
            _uiState.update { it.copy(weather = current, weekWeatherMap = weekly) }
        }
    }

    // ── 내부 ─────────────────────────────────────────────────────

    private fun navigateToMonth(yearMonth: YearMonth) {
        _uiState.update { it.copy(currentMonth = yearMonth, isLoading = true) }
        loadMonth(yearMonth)
    }

    private fun loadMonth(yearMonth: YearMonth) {
        viewModelScope.launch(ioDispatcher) {
            val calendarIds = _uiState.value.enabledCalendarIds
            val currentAsync = async {
                runCatching { calendarRepo.fetchEventsForMonth(yearMonth, calendarIds) }.getOrElse { emptyMap() }
            }
            val nextAsync = async {
                runCatching { calendarRepo.fetchEventsForMonth(yearMonth.plusMonths(1), calendarIds) }.getOrElse { emptyMap() }
            }
            val merged = currentAsync.await() + nextAsync.await()
            _uiState.update { it.copy(eventsForMonth = merged, isLoading = false) }
        }
    }
}

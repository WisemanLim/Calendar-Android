package com.wisecal.android.ui.copy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisecal.android.data.CalendarRepository
import com.wisecal.android.model.CalendarEvent
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class EventCopyViewModel(
    private val calendarRepo: CalendarRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<EventCopyState?>(null)
    val state: StateFlow<EventCopyState?> = _state.asStateFlow()

    private val _saved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saved: SharedFlow<Unit> = _saved.asSharedFlow()

    fun init(event: CalendarEvent, targetDate: LocalDate) {
        if (_state.value == null) {
            _state.value = EventCopyState(sourceEvent = event, targetDate = targetDate)
        }
    }

    fun update(transform: (EventCopyState) -> EventCopyState) {
        _state.update { it?.let(transform) }
    }

    fun save() {
        val s = _state.value?.takeIf { it.canSave } ?: return
        viewModelScope.launch {
            _state.update { it?.copy(isSaving = true) }
            calendarRepo.addEvent(
                title      = s.resolvedTitle,
                notes      = s.resolvedNotes,
                startTime  = s.resolvedStartTime,
                endTime    = s.resolvedEndTime,
                isAllDay   = s.sourceEvent.isAllDay,
                location   = s.sourceEvent.location,
                calendarId = s.sourceEvent.calendarId,
            )
            _state.update { it?.copy(isSaving = false) }
            _saved.emit(Unit)
        }
    }
}

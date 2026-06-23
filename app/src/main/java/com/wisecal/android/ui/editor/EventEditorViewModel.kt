package com.wisecal.android.ui.editor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wisecal.android.data.CalendarRepository
import com.wisecal.android.data.EventImageStore
import com.wisecal.android.logic.PlannerLogic
import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

sealed interface EditorResult {
    object Success : EditorResult
    data class Error(val message: String) : EditorResult
}

class EventEditorViewModel(
    private val calendarRepo: CalendarRepository,
    private val onSaved: () -> Unit,
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {

    private val _state = MutableStateFlow<EventEditorState?>(null)
    val state: StateFlow<EventEditorState?> = _state.asStateFlow()

    private val _result = MutableSharedFlow<EditorResult>()
    val result: SharedFlow<EditorResult> = _result.asSharedFlow()

    /** 새 이벤트 모드 초기화. */
    fun initNew(mode: PlannerMode, date: LocalDate) {
        _state.value = EventEditorState(mode = mode, date = date)
    }

    /** 기존 이벤트 편집 모드 초기화. */
    fun initEdit(mode: PlannerMode, event: CalendarEvent) {
        val todos = if (mode == PlannerMode.TODO)
            PlannerLogic.decodeTodos(event.notes ?: "").ifEmpty {
                listOf(PlannerLogic.TodoItem("", false))
            }
        else
            listOf(PlannerLogic.TodoItem("", false))

        _state.value = EventEditorState(
            mode           = mode,
            date           = event.date,
            title          = event.title,
            diaryBody      = if (mode == PlannerMode.DIARY) event.notes ?: "" else "",
            todos          = todos,
            startTime      = event.startTime,
            endTime        = event.endTime,
            dueDateTime    = event.startTime,
            location       = event.location ?: "",
            memo           = if (mode == PlannerMode.PERSONAL_EVENT) event.notes ?: "" else "",
            isEditMode     = true,
            editingEventId = event.id,
        )
    }

    fun update(transform: (EventEditorState) -> EventEditorState) {
        _state.update { it?.let(transform) }
    }

    fun save(calendarId: Long, diaryOpenedAt: LocalDateTime = LocalDateTime.now()) {
        val s = _state.value ?: return
        if (!s.canSave) return
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                val startTime = if (s.mode == PlannerMode.DIARY) diaryOpenedAt else s.startTime
                val endTime   = if (s.mode == PlannerMode.DIARY) LocalDateTime.now() else s.endTime
                if (s.isEditMode && s.editingEventId != null) {
                    calendarRepo.updateEvent(
                        eventId   = s.editingEventId,
                        title     = s.resolvedTitle,
                        notes     = s.resolvedNotes,
                        startTime = startTime,
                        endTime   = endTime,
                        isAllDay  = false,
                        location  = s.location.ifBlank { null },
                    )
                } else {
                    val eventId = calendarRepo.addEvent(
                        title      = s.resolvedTitle,
                        notes      = s.resolvedNotes,
                        startTime  = startTime,
                        endTime    = endTime,
                        isAllDay   = false,
                        location   = s.location.ifBlank { null },
                        calendarId = calendarId,
                        rrule      = s.rrule,
                    )
                    if (eventId != null && s.photoUri != null) {
                        val ts = System.currentTimeMillis()
                        val dest = EventImageStore.file(appContext.filesDir, eventId, ts)
                        appContext.contentResolver.openInputStream(s.photoUri)?.use { input ->
                            dest.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                }
            }.onSuccess {
                withContext(Dispatchers.Main) { onSaved() }
                _result.emit(EditorResult.Success)
            }.onFailure { e ->
                _result.emit(EditorResult.Error(e.message ?: "저장 실패"))
            }
        }
    }

    fun deleteEvent(eventId: Long) {
        viewModelScope.launch(ioDispatcher) {
            runCatching { calendarRepo.deleteEvent(eventId) }
                .onSuccess {
                    onSaved()
                    _result.emit(EditorResult.Success)
                }
                .onFailure { e ->
                    _result.emit(EditorResult.Error(e.message ?: "삭제 실패"))
                }
        }
    }

    /** 이벤트 복사 (내용 포함/제외). iOS ContextMenu 복사 대응. */
    fun copyEvent(event: CalendarEvent, withContent: Boolean, calendarId: Long) {
        viewModelScope.launch(ioDispatcher) {
            calendarRepo.addEvent(
                title      = event.title,
                notes      = if (withContent) event.notes else null,
                startTime  = event.startTime,
                endTime    = event.endTime,
                isAllDay   = event.isAllDay,
                location   = event.location,
                calendarId = calendarId,
                rrule      = null,
            )
            withContext(Dispatchers.Main) { onSaved() }
        }
    }
}

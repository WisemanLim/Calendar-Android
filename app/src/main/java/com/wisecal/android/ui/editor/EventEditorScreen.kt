package com.wisecal.android.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.PlannerMode

/**
 * 이벤트 추가/편집 전체 화면.
 * iOS NavigationStack + EventEditorView 대응.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditorScreen(
    viewModel: EventEditorViewModel,
    templates: List<EventTemplate>,
    defaultCalendarId: Long,
    onDismiss: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val s = state ?: return

    LaunchedEffect(viewModel.result) {
        viewModel.result.collect { result ->
            if (result is EditorResult.Success) onDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${if (s.isEditMode) "편집" else "새"} ${s.mode.title}") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.save(defaultCalendarId) },
                        enabled = s.canSave,
                    ) {
                        Text("저장")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            when (s.mode) {
                PlannerMode.DIARY -> DiaryEditorSection(
                    state = s,
                    onTitleChange = { v -> viewModel.update { st -> st.copy(title = v) } },
                    onBodyChange  = { v -> viewModel.update { st -> st.copy(diaryBody = v) } },
                )
                PlannerMode.TODO -> TodoEditorSection(
                    state = s,
                    onTitleChange       = { v  -> viewModel.update { st -> st.copy(title = v) } },
                    onTodosChange       = { v  -> viewModel.update { st -> st.copy(todos = v) } },
                    onDueDateTimeChange = { v  -> viewModel.update { st -> st.copy(dueDateTime = v) } },
                )
                PlannerMode.PERSONAL_EVENT -> PersonalEventEditorSection(
                    state = s,
                    templates = templates,
                    onCategoryChange  = { cat -> viewModel.update { st -> st.copy(selectedCategory = cat, selectedTemplate = null) } },
                    onTemplateSelect  = { tmpl ->
                        val start = s.date.atTime(tmpl.defaultStartHour, 0)
                        val end   = if (tmpl.defaultDurationMinutes > 0)
                            start.plusMinutes(tmpl.defaultDurationMinutes.toLong())
                        else start
                        viewModel.update { st -> st.copy(
                            selectedTemplate = tmpl,
                            title = tmpl.fixedDisplayTitle ?: tmpl.titleFormat,
                            startTime = start,
                            endTime = end,
                        ) }
                    },
                    onTitleChange     = { v -> viewModel.update { st -> st.copy(title = v) } },
                    onStartTimeChange = { v -> viewModel.update { st -> st.copy(startTime = v) } },
                    onEndTimeChange   = { v -> viewModel.update { st -> st.copy(endTime = v) } },
                    onLocationChange  = { v -> viewModel.update { st -> st.copy(location = v) } },
                    onRepeatToggle          = { v -> viewModel.update { st -> st.copy(repeatEnabled = v) } },
                    onRepeatStartDateChange = { v -> viewModel.update { st -> st.copy(repeatStartDate = v) } },
                    onRepeatEndDateChange   = { v -> viewModel.update { st -> st.copy(repeatEndDate = v) } },
                )
            }
        }
    }
}

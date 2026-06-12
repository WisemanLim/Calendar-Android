package com.wisecal.android.ui.editor

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREAN)
private val dateFormatter  = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREAN)

/**
 * PERSONAL_EVENT 모드 에디터 섹션.
 * 카테고리 탭 → 템플릿 선택 → 제목 편집 → 시간 → 반복 기간.
 */
@Composable
fun PersonalEventEditorSection(
    state: EventEditorState,
    templates: List<EventTemplate>,
    onCategoryChange: (TemplateCategory) -> Unit,
    onTemplateSelect: (EventTemplate) -> Unit,
    onTitleChange: (String) -> Unit,
    onStartTimeChange: (LocalDateTime) -> Unit,
    onEndTimeChange: (LocalDateTime) -> Unit,
    onLocationChange: (String) -> Unit,
    onRepeatToggle: (Boolean) -> Unit,
    onRepeatStartDateChange: (LocalDate) -> Unit = {},
    onRepeatEndDateChange: (LocalDate) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        // ── 카테고리 탭 ────────────────────────────────────────────
        val currentCategory = state.selectedTemplate?.category ?: state.selectedCategory
        CategoryTabRow(
            selected = currentCategory,
            onSelect = onCategoryChange,
        )
        Spacer(modifier = Modifier.height(8.dp))

        // ── 템플릿 목록 ────────────────────────────────────────────
        val filtered = templates.filter { it.category == currentCategory }
        Text(
            text = "템플릿",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        filtered.forEach { tmpl ->
            val displayTitle = tmpl.fixedDisplayTitle ?: tmpl.titleFormat
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = state.selectedTemplate?.id == tmpl.id,
                        onClick = { onTemplateSelect(tmpl) },
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(displayTitle, modifier = Modifier.weight(1f))
                if (state.selectedTemplate?.id == tmpl.id) {
                    RadioButton(selected = true, onClick = { onTemplateSelect(tmpl) })
                }
            }
        }

        // ── 선택된 템플릿 세부 설정 ────────────────────────────────
        if (state.selectedTemplate != null) {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("제목") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            when (state.selectedTemplate.category) {
                TemplateCategory.MEDICATION -> {
                    Text("복용 시간: ${state.startTime.format(timeFormatter)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    RepeatSection(
                        state = state,
                        onRepeatToggle = onRepeatToggle,
                        onRepeatStartDateChange = onRepeatStartDateChange,
                        onRepeatEndDateChange = onRepeatEndDateChange,
                    )
                }
                TemplateCategory.APPOINTMENT -> {
                    Text("시작: ${state.startTime.format(timeFormatter)}")
                    Text("종료: ${state.endTime.format(timeFormatter)}")
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.location,
                        onValueChange = onLocationChange,
                        label = { Text("장소 (선택)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                TemplateCategory.FIXED -> {
                    Text("시작: ${state.startTime.format(timeFormatter)}")
                    Text("종료: ${state.endTime.format(timeFormatter)}")
                    Spacer(modifier = Modifier.height(8.dp))
                    RepeatSection(
                        state = state,
                        onRepeatToggle = onRepeatToggle,
                        onRepeatStartDateChange = onRepeatStartDateChange,
                        onRepeatEndDateChange = onRepeatEndDateChange,
                    )
                }
            }
        }
    }
}

// ── 반복 기간 섹션 ────────────────────────────────────────────────────────

@Composable
private fun RepeatSection(
    state: EventEditorState,
    onRepeatToggle: (Boolean) -> Unit,
    onRepeatStartDateChange: (LocalDate) -> Unit,
    onRepeatEndDateChange: (LocalDate) -> Unit,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "매년 반복",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = state.repeatEnabled, onCheckedChange = onRepeatToggle)
        }

        if (state.repeatEnabled) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "반복 기간",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DatePickerButton(
                    label = "시작",
                    date = state.repeatStartDate,
                    onDateSelected = onRepeatStartDateChange,
                    modifier = Modifier.weight(1f),
                )
                Text("~", style = MaterialTheme.typography.bodyMedium)
                DatePickerButton(
                    label = "종료",
                    date = state.repeatEndDate,
                    onDateSelected = onRepeatEndDateChange,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "RRULE: FREQ=YEARLY;INTERVAL=1;UNTIL=${state.repeatEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DatePickerButton(
    label: String,
    date: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    OutlinedButton(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day -> onDateSelected(LocalDate.of(year, month + 1, day)) },
                date.year,
                date.monthValue - 1,
                date.dayOfMonth,
            ).show()
        },
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Text(
            text = "$label: ${date.format(dateFormatter)}",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun CategoryTabRow(
    selected: TemplateCategory,
    onSelect: (TemplateCategory) -> Unit,
) {
    TabRow(selectedTabIndex = TemplateCategory.entries.indexOf(selected)) {
        TemplateCategory.entries.forEach { cat ->
            Tab(
                selected = cat == selected,
                onClick = { onSelect(cat) },
                text = { Text(cat.title) },
            )
        }
    }
}

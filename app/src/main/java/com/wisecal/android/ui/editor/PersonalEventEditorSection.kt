package com.wisecal.android.ui.editor

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import java.time.DayOfWeek
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
@OptIn(ExperimentalLayoutApi::class)
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
    onRepeatFrequencyChange: (RepeatFrequency) -> Unit = {},
    onRepeatDaysOfWeekChange: (Set<DayOfWeek>) -> Unit = {},
    onRepeatStartDateChange: (LocalDate) -> Unit = {},
    onRepeatEndDateChange: (LocalDate) -> Unit = {},
    onMemoChange: (String) -> Unit = {},
    onMedicationPresetChange: (MedicationPreset) -> Unit = {},
    onPickPhotoFromGallery: () -> Unit = {},
    onTakePhoto: () -> Unit = {},
    onRemovePhoto: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {

        // 편집 모드: 기존 이벤트 데이터가 있으므로 템플릿 선택 스킵, 폼 바로 표시.
        if (!state.isEditMode) {
            // ── 카테고리 탭 ─────────────────────────────────────────
            val currentCategory = state.selectedTemplate?.category ?: state.selectedCategory
            CategoryTabRow(
                selected = currentCategory,
                onSelect = onCategoryChange,
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── 템플릿 목록 ─────────────────────────────────────────
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
        }

        // ── 세부 설정 (편집 모드이거나 템플릿 선택된 경우) ──────────
        if (state.isEditMode || state.selectedTemplate != null) {
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

            when (state.selectedTemplate?.category) {
                TemplateCategory.MEDICATION -> {
                    var showTimePicker by remember { mutableStateOf(false) }

                    Text(
                        "복용 시간",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MedicationPreset.entries.forEach { preset ->
                            FilterChip(
                                selected = state.medicationPreset == preset,
                                onClick = {
                                    onMedicationPresetChange(preset)
                                    preset.hour?.let { h ->
                                        onStartTimeChange(state.startTime.withHour(h).withMinute(0))
                                    }
                                },
                                label = { Text(preset.label) },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { showTimePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("복용 시간: ${state.startTime.format(timeFormatter)}")
                    }
                    if (showTimePicker) {
                        MedicationTimePickerDialog(
                            initialHour = state.startTime.hour,
                            initialMinute = state.startTime.minute,
                            onConfirm = { h, m ->
                                onStartTimeChange(state.startTime.withHour(h).withMinute(m))
                                onMedicationPresetChange(MedicationPreset.CUSTOM)
                                showTimePicker = false
                            },
                            onDismiss = { showTimePicker = false },
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    RepeatSection(
                        state = state,
                        onRepeatToggle = onRepeatToggle,
                        onRepeatFrequencyChange = onRepeatFrequencyChange,
                        onRepeatDaysOfWeekChange = onRepeatDaysOfWeekChange,
                        onRepeatStartDateChange = onRepeatStartDateChange,
                        onRepeatEndDateChange = onRepeatEndDateChange,
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "사진",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (state.photoUri != null) {
                        AsyncImage(
                            model = state.photoUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextButton(
                            onClick = onRemovePhoto,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
                        ) { Text("사진 제거") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onPickPhotoFromGallery) { Text("앨범") }
                        OutlinedButton(onClick = onTakePhoto) { Text("카메라") }
                    }
                }
                TemplateCategory.FIXED -> {
                    var showFixedStartPicker by remember { mutableStateOf(false) }
                    var showFixedEndPicker by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { showFixedStartPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("시작: ${state.startTime.format(timeFormatter)}")
                    }
                    OutlinedButton(onClick = { showFixedEndPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("종료: ${state.endTime.format(timeFormatter)}")
                    }
                    if (showFixedStartPicker) {
                        MedicationTimePickerDialog(
                            initialHour = state.startTime.hour,
                            initialMinute = state.startTime.minute,
                            onConfirm = { h, m ->
                                onStartTimeChange(state.startTime.withHour(h).withMinute(m))
                                showFixedStartPicker = false
                            },
                            onDismiss = { showFixedStartPicker = false },
                        )
                    }
                    if (showFixedEndPicker) {
                        MedicationTimePickerDialog(
                            initialHour = state.endTime.hour,
                            initialMinute = state.endTime.minute,
                            onConfirm = { h, m ->
                                onEndTimeChange(state.endTime.withHour(h).withMinute(m))
                                showFixedEndPicker = false
                            },
                            onDismiss = { showFixedEndPicker = false },
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    RepeatSection(
                        state = state,
                        onRepeatToggle = onRepeatToggle,
                        onRepeatFrequencyChange = onRepeatFrequencyChange,
                        onRepeatDaysOfWeekChange = onRepeatDaysOfWeekChange,
                        onRepeatStartDateChange = onRepeatStartDateChange,
                        onRepeatEndDateChange = onRepeatEndDateChange,
                    )
                }
                else -> {
                    // APPOINTMENT 또는 편집 모드(template = null): 시간 + 장소 표시
                    var showApptStartPicker by remember { mutableStateOf(false) }
                    var showApptEndPicker by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { showApptStartPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("시작: ${state.startTime.format(timeFormatter)}")
                    }
                    OutlinedButton(onClick = { showApptEndPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("종료: ${state.endTime.format(timeFormatter)}")
                    }
                    if (showApptStartPicker) {
                        MedicationTimePickerDialog(
                            initialHour = state.startTime.hour,
                            initialMinute = state.startTime.minute,
                            onConfirm = { h, m ->
                                onStartTimeChange(state.startTime.withHour(h).withMinute(m))
                                showApptStartPicker = false
                            },
                            onDismiss = { showApptStartPicker = false },
                        )
                    }
                    if (showApptEndPicker) {
                        MedicationTimePickerDialog(
                            initialHour = state.endTime.hour,
                            initialMinute = state.endTime.minute,
                            onConfirm = { h, m ->
                                onEndTimeChange(state.endTime.withHour(h).withMinute(m))
                                showApptEndPicker = false
                            },
                            onDismiss = { showApptEndPicker = false },
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = state.location,
                        onValueChange = onLocationChange,
                        label = { Text("장소 (선택)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            // 메모: 편집 모드에서 기존 내용 있거나 편집 중인 경우 항상 표시
            if (state.isEditMode || state.memo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.memo,
                    onValueChange = onMemoChange,
                    label = { Text("메모 (선택)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// ── 반복 기간 섹션 ────────────────────────────────────────────────────────

private val DOW_ORDER = listOf(
    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY,
)
private val DOW_LABELS = mapOf(
    DayOfWeek.SUNDAY to "일", DayOfWeek.MONDAY to "월", DayOfWeek.TUESDAY to "화",
    DayOfWeek.WEDNESDAY to "수", DayOfWeek.THURSDAY to "목", DayOfWeek.FRIDAY to "금",
    DayOfWeek.SATURDAY to "토",
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RepeatSection(
    state: EventEditorState,
    onRepeatToggle: (Boolean) -> Unit,
    onRepeatFrequencyChange: (RepeatFrequency) -> Unit,
    onRepeatDaysOfWeekChange: (Set<DayOfWeek>) -> Unit,
    onRepeatStartDateChange: (LocalDate) -> Unit,
    onRepeatEndDateChange: (LocalDate) -> Unit,
) {
    Column {
        // 반복 on/off 토글
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "반복",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = state.repeatEnabled, onCheckedChange = onRepeatToggle)
        }

        if (state.repeatEnabled) {
            Spacer(modifier = Modifier.height(8.dp))

            // 반복 주기 선택: 매일 | 매주 | 매년
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RepeatFrequency.entries.forEach { freq ->
                    FilterChip(
                        selected = state.repeatFrequency == freq,
                        onClick = { onRepeatFrequencyChange(freq) },
                        label = { Text(freq.label) },
                    )
                }
            }

            // 매주 선택 시 요일 다중 선택
            if (state.repeatFrequency == RepeatFrequency.WEEKLY) {
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    DOW_ORDER.forEach { dow ->
                        val selected = dow in state.repeatDaysOfWeek
                        FilterChip(
                            selected = selected,
                            onClick = {
                                val updated = if (selected)
                                    state.repeatDaysOfWeek - dow
                                else
                                    state.repeatDaysOfWeek + dow
                                onRepeatDaysOfWeekChange(updated)
                            },
                            label = { Text(DOW_LABELS[dow] ?: "") },
                        )
                    }
                }
            }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MedicationTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        },
        text = { TimePicker(state = timePickerState) },
    )
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

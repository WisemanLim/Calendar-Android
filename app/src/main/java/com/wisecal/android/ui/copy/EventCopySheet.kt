package com.wisecal.android.ui.copy

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val displayFmt = DateTimeFormatter.ofPattern("yyyy.M.d (E)", Locale.KOREAN)

/**
 * 이벤트 복사 BottomSheet.
 * iOS EventCopySheet 대응.
 * includeNotes 토글 + 날짜 선택 + 미리보기 → 저장.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventCopySheet(
    viewModel: EventCopyViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val s = state ?: return

    LaunchedEffect(viewModel) {
        viewModel.saved.collect { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("이벤트 복사", style = MaterialTheme.typography.titleMedium)

            // 날짜 선택 — DatePicker 다이얼로그
            var showDatePicker by remember { mutableStateOf(false) }
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("복사할 날짜: ${s.targetDate.format(displayFmt)}")
            }
            if (showDatePicker) {
                DatePickerDialog(
                    targetDate = s.targetDate,
                    onConfirm = { date ->
                        viewModel.update { it.copy(targetDate = date) }
                        showDatePicker = false
                    },
                    onDismiss = { showDatePicker = false },
                )
            }

            // 미리보기
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("제목: ${s.resolvedTitle}", style = MaterialTheme.typography.bodyMedium)
                if (s.sourceEvent.notes != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("메모 포함", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = s.includeNotes,
                            onCheckedChange = { v -> viewModel.update { it.copy(includeNotes = v) } },
                        )
                    }
                }
            }

            Button(
                onClick = viewModel::save,
                enabled = s.canSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (s.isSaving) CircularProgressIndicator(modifier = Modifier.size(18.dp))
                else Text("복사 저장")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
    targetDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = targetDate
            .atStartOfDay(java.time.ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { ms ->
                    val date = java.time.Instant.ofEpochMilli(ms)
                        .atZone(java.time.ZoneOffset.UTC)
                        .toLocalDate()
                    onConfirm(date)
                }
            }) { Text("확인") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } },
    ) {
        DatePicker(state = state)
    }
}

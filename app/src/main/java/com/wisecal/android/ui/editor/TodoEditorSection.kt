package com.wisecal.android.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wisecal.android.logic.PlannerLogic
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 (E)", Locale.KOREAN)

/**
 * TODO 모드 에디터 섹션.
 * 제목 + 체크박스 목록 + 마감일.
 * iOS EventEditorView todoEditor 대응.
 */
@Composable
fun TodoEditorSection(
    state: EventEditorState,
    onTitleChange: (String) -> Unit,
    onTodosChange: (List<PlannerLogic.TodoItem>) -> Unit,
    onDueDateTimeChange: (java.time.LocalDateTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("제목 (선택)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "할 일 목록",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))

        state.todos.forEachIndexed { index, item ->
            TodoItemRow(
                item = item,
                onTextChange = { text ->
                    val updated = state.todos.toMutableList()
                    updated[index] = item.copy(text = text)
                    onTodosChange(updated)
                },
                onToggle = {
                    val updated = state.todos.toMutableList()
                    updated[index] = item.copy(done = !item.done)
                    onTodosChange(updated)
                },
                onDelete = {
                    onTodosChange(state.todos.toMutableList().also { it.removeAt(index) })
                },
            )
        }

        TextButton(
            onClick = {
                onTodosChange(state.todos + PlannerLogic.TodoItem("", false))
            },
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("항목 추가")
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "마감일: ${state.dueDateTime.format(dateFormatter)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun TodoItemRow(
    item: PlannerLogic.TodoItem,
    onTextChange: (String) -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Checkbox(checked = item.done, onCheckedChange = { onToggle() })
        OutlinedTextField(
            value = item.text,
            onValueChange = onTextChange,
            singleLine = true,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Close,
                contentDescription = "삭제",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

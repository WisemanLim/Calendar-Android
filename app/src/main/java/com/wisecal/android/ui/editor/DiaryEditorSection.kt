package com.wisecal.android.ui.editor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * DIARY 모드 에디터 섹션.
 * 제목 + 본문 멀티라인.
 * iOS EventEditorView diaryEditor 대응.
 */
@Composable
fun DiaryEditorSection(
    state: EventEditorState,
    onTitleChange: (String) -> Unit,
    onBodyChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = { Text("제목") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = state.diaryBody,
            onValueChange = onBodyChange,
            label = { Text("일기 본문") },
            minLines = 6,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 160.dp),
        )
    }
}

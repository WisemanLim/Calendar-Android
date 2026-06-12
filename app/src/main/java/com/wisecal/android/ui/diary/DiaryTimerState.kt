package com.wisecal.android.ui.diary

import java.time.LocalDateTime

/**
 * diary 모드 경과시간 상태.
 * iOS EventEditorView elapsed-timer 대응.
 * 에디터 오픈 시각을 startedAt 으로 저장, 저장 버튼 누를 때 endTime = startedAt + elapsed.
 */
data class DiaryTimerState(
    val startedAt: LocalDateTime,
    val elapsedSeconds: Long = 0L,
) {
    val displayTime: String
        get() = if (elapsedSeconds < 3600) {
            "%02d:%02d".format(elapsedSeconds / 60, elapsedSeconds % 60)
        } else {
            "%d:%02d:%02d".format(
                elapsedSeconds / 3600,
                (elapsedSeconds % 3600) / 60,
                elapsedSeconds % 60,
            )
        }

    val endTime: LocalDateTime
        get() = startedAt.plusSeconds(elapsedSeconds)
}

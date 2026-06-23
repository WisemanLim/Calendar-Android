package com.wisecal.android.ui.editor

import android.net.Uri
import com.wisecal.android.logic.PlannerLogic
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.model.TemplateCategory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class MedicationPreset(val label: String, val hour: Int?) {
    MORNING("아침", 7),
    LUNCH("점심", 12),
    EVENING("저녁", 18),
    CUSTOM("직접", null),
}

enum class RepeatFrequency(val label: String) {
    DAILY("매일"),
    WEEKLY("매주"),
    YEARLY("매년"),
}

/**
 * 이벤트 추가/편집 화면 전체 상태 (순수 data class).
 * iOS EventEditorView @State 묶음 대응.
 */
data class EventEditorState(
    val mode: PlannerMode,
    val date: LocalDate,
    // ── 공통 ──────────────────────────────────────────────────────
    val title: String = "",
    // ── DIARY ─────────────────────────────────────────────────────
    val diaryBody: String = "",
    // ── TODO ──────────────────────────────────────────────────────
    val todos: List<PlannerLogic.TodoItem> = listOf(PlannerLogic.TodoItem("", false)),
    val dueDateTime: LocalDateTime = date.atTime(23, 59),
    // ── PERSONAL_EVENT ────────────────────────────────────────────
    val startTime: LocalDateTime = date.atTime(9, 0),
    val endTime: LocalDateTime = date.atTime(10, 0),
    val location: String = "",
    val memo: String = "",
    val selectedCategory: TemplateCategory = TemplateCategory.MEDICATION,
    val selectedTemplate: EventTemplate? = null,
    val repeatEnabled: Boolean = false,
    val repeatFrequency: RepeatFrequency = RepeatFrequency.YEARLY,
    val repeatDaysOfWeek: Set<DayOfWeek> = emptySet(),
    val repeatStartDate: LocalDate = date,
    val repeatEndDate: LocalDate = date.plusYears(1),
    val medicationPreset: MedicationPreset = MedicationPreset.MORNING,
    val photoUri: Uri? = null,
    // ── 편집 모드 ──────────────────────────────────────────────────
    val isEditMode: Boolean = false,
    val editingEventId: Long? = null,
) {
    /** 저장 버튼 활성화 조건 (iOS canSave 대응). */
    val canSave: Boolean
        get() = when (mode) {
            PlannerMode.DIARY          -> title.isNotEmpty() || diaryBody.isNotEmpty()
            PlannerMode.TODO           -> todos.any { it.text.trim().isNotEmpty() }
            PlannerMode.PERSONAL_EVENT -> title.isNotEmpty() && (isEditMode || selectedTemplate != null)
        }

    /** 저장 시 사용할 최종 제목. */
    val resolvedTitle: String
        get() = when (mode) {
            PlannerMode.DIARY          -> title.ifEmpty { "일기" }
            PlannerMode.TODO           -> title.ifEmpty { "할 일" }
            PlannerMode.PERSONAL_EVENT -> PlannerLogic.substituteTitlePatterns(title, date)
        }

    /** 저장 시 사용할 메모 문자열. null = 메모 없음. */
    val resolvedNotes: String?
        get() = when (mode) {
            PlannerMode.DIARY -> diaryBody.ifEmpty { null }
            PlannerMode.TODO  -> {
                val valid = todos.filter { it.text.trim().isNotEmpty() }
                valid.takeIf { it.isNotEmpty() }?.let { PlannerLogic.encodeTodos(it) }
            }
            PlannerMode.PERSONAL_EVENT -> memo.ifEmpty { null }
        }

    /** 제목에 날짜 패턴 포함 여부. */
    val hasTitleDatePattern: Boolean
        get() = PlannerLogic.hasTitleDatePattern(title)

    /**
     * CalendarContract.Events.RRULE 값 (RFC 2445).
     * null = 반복 없음.
     * MEDICATION: 날짜 패턴 없거나 반복 설정 시 yearly.
     * APPOINTMENT: 날짜 패턴 없으면 yearly.
     * FIXED: 날짜 패턴 없거나 반복 설정 시 yearly.
     */
    val rrule: String?
        get() {
            if (mode != PlannerMode.PERSONAL_EVENT) return null
            val tmpl = selectedTemplate ?: return null
            return when (tmpl.category) {
                TemplateCategory.MEDICATION ->
                    if (repeatEnabled || !hasTitleDatePattern) buildRrule() else null
                TemplateCategory.APPOINTMENT ->
                    if (!hasTitleDatePattern) buildRrule() else null
                TemplateCategory.FIXED -> when {
                    repeatEnabled        -> buildRrule()
                    !hasTitleDatePattern -> buildRrule()
                    else                 -> null
                }
            }
        }

    private fun buildRrule(): String {
        val until = if (repeatEnabled) {
            ";UNTIL=${repeatEndDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))}T000000Z"
        } else ""
        return when (repeatFrequency) {
            RepeatFrequency.DAILY  -> "FREQ=DAILY;INTERVAL=1$until"
            RepeatFrequency.WEEKLY -> {
                val days = repeatDaysOfWeek
                    .sortedBy { it.value }
                    .joinToString(",") { it.toRruleDay() }
                val byDay = if (days.isNotEmpty()) ";BYDAY=$days" else ""
                "FREQ=WEEKLY;INTERVAL=1$byDay$until"
            }
            RepeatFrequency.YEARLY -> "FREQ=YEARLY;INTERVAL=1$until"
        }
    }

    private fun DayOfWeek.toRruleDay() = when (this) {
        DayOfWeek.MONDAY    -> "MO"
        DayOfWeek.TUESDAY   -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY  -> "TH"
        DayOfWeek.FRIDAY    -> "FR"
        DayOfWeek.SATURDAY  -> "SA"
        DayOfWeek.SUNDAY    -> "SU"
    }
}

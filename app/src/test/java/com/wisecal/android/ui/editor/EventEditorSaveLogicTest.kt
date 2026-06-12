package com.wisecal.android.ui.editor

import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.model.TemplateCategory
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class EventEditorSaveLogicTest {

    private val date = LocalDate.of(2026, 6, 11)

    private fun state(
        category: TemplateCategory,
        titleFormat: String = "제목",
        repeatEnabled: Boolean = false,
        repeatEndDate: LocalDate = date.plusYears(1),
    ) = EventEditorState(
        mode = PlannerMode.PERSONAL_EVENT,
        date = date,
        title = titleFormat,
        selectedTemplate = EventTemplate(
            id = "t1", name = "T",
            category = category,
            titleFormat = titleFormat,
        ),
        repeatEnabled = repeatEnabled,
        repeatEndDate = repeatEndDate,
    )

    // ── MEDICATION ────────────────────────────────────────────────

    @Test fun `MEDICATION no date pattern no repeat → yearly RRULE`() {
        val s = state(TemplateCategory.MEDICATION, "아침 복용")
        assertEquals("FREQ=YEARLY;INTERVAL=1", s.rrule)
    }

    @Test fun `MEDICATION with date pattern no repeat → no RRULE`() {
        val s = state(TemplateCategory.MEDICATION, "복용 yyyy-mm-dd")
        assertNull(s.rrule)
    }

    @Test fun `MEDICATION repeat enabled → RRULE with UNTIL`() {
        val end = LocalDate.of(2028, 12, 31)
        val s = state(TemplateCategory.MEDICATION, "아침 복용", repeatEnabled = true, repeatEndDate = end)
        assertEquals("FREQ=YEARLY;INTERVAL=1;UNTIL=20281231T000000Z", s.rrule)
    }

    @Test fun `MEDICATION date pattern but repeat enabled → RRULE`() {
        val s = state(TemplateCategory.MEDICATION, "복용 d###", repeatEnabled = true)
        assertNotNull(s.rrule)
    }

    // ── APPOINTMENT ───────────────────────────────────────────────

    @Test fun `APPOINTMENT no date pattern → yearly RRULE`() {
        val s = state(TemplateCategory.APPOINTMENT, "연인과 약속")
        assertEquals("FREQ=YEARLY;INTERVAL=1", s.rrule)
    }

    @Test fun `APPOINTMENT with date pattern → no RRULE`() {
        val s = state(TemplateCategory.APPOINTMENT, "약속 yyyy-mm-dd")
        assertNull(s.rrule)
    }

    // ── FIXED ─────────────────────────────────────────────────────

    @Test fun `FIXED no date pattern no repeat → yearly RRULE`() {
        val s = state(TemplateCategory.FIXED, "정기점검")
        assertEquals("FREQ=YEARLY;INTERVAL=1", s.rrule)
    }

    @Test fun `FIXED repeat enabled → RRULE with UNTIL`() {
        val end = LocalDate.of(2030, 6, 30)
        val s = state(TemplateCategory.FIXED, "정기점검", repeatEnabled = true, repeatEndDate = end)
        assertEquals("FREQ=YEARLY;INTERVAL=1;UNTIL=20300630T000000Z", s.rrule)
    }

    @Test fun `FIXED with date pattern no repeat → no RRULE`() {
        val s = state(TemplateCategory.FIXED, "점검 yyyy-mm-dd")
        assertNull(s.rrule)
    }

    // ── non-PERSONAL_EVENT ────────────────────────────────────────

    @Test fun `DIARY mode rrule is null`() {
        val s = EventEditorState(mode = PlannerMode.DIARY, date = date)
        assertNull(s.rrule)
    }

    @Test fun `TODO mode rrule is null`() {
        val s = EventEditorState(mode = PlannerMode.TODO, date = date)
        assertNull(s.rrule)
    }

    // ── hasTitleDatePattern ───────────────────────────────────────

    @Test fun `d### pattern detected`() {
        val s = EventEditorState(mode = PlannerMode.PERSONAL_EVENT, date = date, title = "약 복용 d###")
        assertTrue(s.hasTitleDatePattern)
    }

    @Test fun `no pattern → false`() {
        val s = EventEditorState(mode = PlannerMode.PERSONAL_EVENT, date = date, title = "아침 약 복용")
        assertFalse(s.hasTitleDatePattern)
    }
}

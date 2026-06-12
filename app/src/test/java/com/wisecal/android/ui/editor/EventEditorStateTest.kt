package com.wisecal.android.ui.editor

import com.wisecal.android.logic.PlannerLogic
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.model.TemplateCategory
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class EventEditorStateTest {

    private val date = LocalDate.of(2026, 6, 11)

    private fun state(
        mode: PlannerMode = PlannerMode.DIARY,
        title: String = "",
        diaryBody: String = "",
        todos: List<PlannerLogic.TodoItem> = listOf(PlannerLogic.TodoItem("", false)),
        template: EventTemplate? = null,
    ) = EventEditorState(
        mode = mode, date = date,
        title = title, diaryBody = diaryBody,
        todos = todos, selectedTemplate = template,
    )

    private fun template(category: TemplateCategory = TemplateCategory.MEDICATION) =
        EventTemplate(id = "t1", name = "T", category = category, titleFormat = "제목")

    // ── canSave: DIARY ────────────────────────────────────────────

    @Test fun `DIARY canSave true with title`() {
        assertTrue(state(PlannerMode.DIARY, title = "일기 제목").canSave)
    }

    @Test fun `DIARY canSave true with body only`() {
        assertTrue(state(PlannerMode.DIARY, diaryBody = "본문").canSave)
    }

    @Test fun `DIARY canSave false when both empty`() {
        assertFalse(state(PlannerMode.DIARY).canSave)
    }

    // ── canSave: TODO ─────────────────────────────────────────────

    @Test fun `TODO canSave true with non-empty item`() {
        val todos = listOf(PlannerLogic.TodoItem("항목 1", false))
        assertTrue(state(PlannerMode.TODO, todos = todos).canSave)
    }

    @Test fun `TODO canSave false when all items blank`() {
        val todos = listOf(PlannerLogic.TodoItem("   ", false))
        assertFalse(state(PlannerMode.TODO, todos = todos).canSave)
    }

    // ── canSave: PERSONAL_EVENT ───────────────────────────────────

    @Test fun `PERSONAL_EVENT canSave true with template and title`() {
        assertTrue(state(PlannerMode.PERSONAL_EVENT, title = "제목", template = template()).canSave)
    }

    @Test fun `PERSONAL_EVENT canSave false without template`() {
        assertFalse(state(PlannerMode.PERSONAL_EVENT, title = "제목").canSave)
    }

    // ── resolvedTitle ─────────────────────────────────────────────

    @Test fun `DIARY empty title resolves to 일기`() {
        assertEquals("일기", state(PlannerMode.DIARY).resolvedTitle)
    }

    @Test fun `TODO empty title resolves to 할 일`() {
        assertEquals("할 일", state(PlannerMode.TODO).resolvedTitle)
    }

    @Test fun `PERSONAL_EVENT substitutes yyyy-mm-dd pattern`() {
        val s = state(PlannerMode.PERSONAL_EVENT, title = "기록 yyyy-mm-dd")
        assertEquals("기록 2026-06-11", s.resolvedTitle)
    }

    // ── resolvedNotes ─────────────────────────────────────────────

    @Test fun `DIARY diaryBody becomes resolvedNotes`() {
        val s = state(PlannerMode.DIARY, diaryBody = "오늘 좋은 날")
        assertEquals("오늘 좋은 날", s.resolvedNotes)
    }

    @Test fun `TODO todos encode to resolvedNotes`() {
        val todos = listOf(
            PlannerLogic.TodoItem("우유 사기", false),
            PlannerLogic.TodoItem("운동", true),
        )
        val s = state(PlannerMode.TODO, todos = todos)
        assertEquals("- [ ] 우유 사기\n- [x] 운동", s.resolvedNotes)
    }

    @Test fun `TODO all blank todos → resolvedNotes null`() {
        val todos = listOf(PlannerLogic.TodoItem("  ", false))
        assertNull(state(PlannerMode.TODO, todos = todos).resolvedNotes)
    }

    @Test fun `PERSONAL_EVENT resolvedNotes is null`() {
        val s = state(PlannerMode.PERSONAL_EVENT, template = template())
        assertNull(s.resolvedNotes)
    }
}

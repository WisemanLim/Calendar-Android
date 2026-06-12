package com.wisecal.android.ui.template

import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import org.junit.Assert.*
import org.junit.Test

class TemplateStateTest {

    private fun tpl(
        id: String,
        category: TemplateCategory,
        name: String = id,
    ) = EventTemplate(
        id = id,
        name = name,
        category = category,
        titleFormat = name,
        dayOfYear = null,
        defaultStartHour = 9,
        defaultDurationMinutes = 60,
    )

    private val med1 = tpl("m1", TemplateCategory.MEDICATION)
    private val med2 = tpl("m2", TemplateCategory.MEDICATION)
    private val apt1 = tpl("a1", TemplateCategory.APPOINTMENT)
    private val fix1 = tpl("f1", TemplateCategory.FIXED)

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial templates is empty`() {
        assertTrue(TemplateState().templates.isEmpty())
    }

    @Test fun `initial selectedCategory is null`() {
        assertNull(TemplateState().selectedCategory)
    }

    // ── hasTemplates ───────────────────────────────────────────────

    @Test fun `hasTemplates false when empty`() {
        assertFalse(TemplateState().hasTemplates)
    }

    @Test fun `hasTemplates true when templates present`() {
        assertTrue(TemplateState(templates = listOf(med1)).hasTemplates)
    }

    // ── filteredTemplates ──────────────────────────────────────────

    @Test fun `filteredTemplates returns all when selectedCategory null`() {
        val s = TemplateState(templates = listOf(med1, apt1, fix1), selectedCategory = null)
        assertEquals(3, s.filteredTemplates.size)
    }

    @Test fun `filteredTemplates filters by MEDICATION`() {
        val s = TemplateState(templates = listOf(med1, med2, apt1), selectedCategory = TemplateCategory.MEDICATION)
        assertEquals(listOf(med1, med2), s.filteredTemplates)
    }

    @Test fun `filteredTemplates filters by APPOINTMENT`() {
        val s = TemplateState(templates = listOf(med1, apt1), selectedCategory = TemplateCategory.APPOINTMENT)
        assertEquals(listOf(apt1), s.filteredTemplates)
    }

    @Test fun `filteredTemplates empty when no match`() {
        val s = TemplateState(templates = listOf(med1), selectedCategory = TemplateCategory.FIXED)
        assertTrue(s.filteredTemplates.isEmpty())
    }

    @Test fun `filteredTemplates preserves insertion order`() {
        val s = TemplateState(templates = listOf(med2, med1), selectedCategory = TemplateCategory.MEDICATION)
        assertEquals(listOf(med2, med1), s.filteredTemplates)
    }

    @Test fun `filteredTemplates updates when selectedCategory changes via copy`() {
        val base = TemplateState(templates = listOf(med1, apt1))
        val filtered = base.copy(selectedCategory = TemplateCategory.APPOINTMENT)
        assertEquals(listOf(apt1), filtered.filteredTemplates)
    }

    // ── categoryGroups ─────────────────────────────────────────────

    @Test fun `categoryGroups empty when no templates`() {
        assertTrue(TemplateState().categoryGroups.isEmpty())
    }

    @Test fun `categoryGroups contains MEDICATION key`() {
        val s = TemplateState(templates = listOf(med1, med2, apt1))
        assertTrue(s.categoryGroups.containsKey(TemplateCategory.MEDICATION))
        assertEquals(2, s.categoryGroups[TemplateCategory.MEDICATION]?.size)
    }

    @Test fun `categoryGroups has correct key count`() {
        val s = TemplateState(templates = listOf(med1, apt1, fix1))
        assertEquals(3, s.categoryGroups.keys.size)
    }
}

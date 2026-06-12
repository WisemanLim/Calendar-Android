package com.wisecal.android.data

import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import com.wisecal.android.model.defaultTemplates
import org.junit.Assert.*
import org.junit.Test

class EventTemplateTest {

    @Test
    fun `fixedDisplayTitle null when category is not FIXED`() {
        val t = EventTemplate(
            name = "아침 복용",
            category = TemplateCategory.MEDICATION,
            titleFormat = "아침 약 복용",
            dayOfYear = 100,
        )
        assertNull(t.fixedDisplayTitle)
    }

    @Test
    fun `fixedDisplayTitle null when dayOfYear is null`() {
        val t = EventTemplate(
            name = "정기작업",
            category = TemplateCategory.FIXED,
            titleFormat = "정기작업",
            dayOfYear = null,
        )
        assertNull(t.fixedDisplayTitle)
    }

    @Test
    fun `fixedDisplayTitle returns bracketed day format`() {
        val t = EventTemplate(
            name = "정기작업",
            category = TemplateCategory.FIXED,
            titleFormat = "정기점검",
            dayOfYear = 162,
        )
        assertEquals("[#162] 정기점검", t.fixedDisplayTitle)
    }

    @Test
    fun `defaultTemplates has 9 entries`() {
        assertEquals(9, defaultTemplates.size)
    }

    @Test
    fun `defaultTemplates all have unique ids`() {
        val ids = defaultTemplates.map { it.id }
        assertEquals(ids.distinct().size, ids.size)
    }

    @Test
    fun `defaultTemplates category distribution correct`() {
        val medication  = defaultTemplates.count { it.category == TemplateCategory.MEDICATION }
        val appointment = defaultTemplates.count { it.category == TemplateCategory.APPOINTMENT }
        val fixed       = defaultTemplates.count { it.category == TemplateCategory.FIXED }
        assertEquals(5, medication)
        assertEquals(3, appointment)
        assertEquals(1, fixed)
    }

    @Test
    fun `defaultTemplates MEDICATION templates have 0 duration`() {
        defaultTemplates.filter { it.category == TemplateCategory.MEDICATION }.forEach {
            assertEquals("MEDICATION ${it.name} duration", 0, it.defaultDurationMinutes)
        }
    }

    @Test
    fun `defaultTemplates APPOINTMENT templates have positive duration`() {
        defaultTemplates.filter { it.category == TemplateCategory.APPOINTMENT }.forEach {
            assertTrue("APPOINTMENT ${it.name} duration > 0", it.defaultDurationMinutes > 0)
        }
    }

    @Test
    fun `EventTemplate equality by value`() {
        val id = "test-id-123"
        val a = EventTemplate(id = id, name = "X", category = TemplateCategory.MEDICATION, titleFormat = "X")
        val b = EventTemplate(id = id, name = "X", category = TemplateCategory.MEDICATION, titleFormat = "X")
        assertEquals(a, b)
    }

    @Test
    fun `TemplateCategory fromString returns default on unknown`() {
        assertEquals(TemplateCategory.MEDICATION, TemplateCategory.fromString("UNKNOWN"))
    }

    @Test
    fun `TemplateCategory fromString round-trips all values`() {
        TemplateCategory.entries.forEach {
            assertEquals(it, TemplateCategory.fromString(it.name))
        }
    }
}

package com.wisecal.android.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import com.wisecal.android.model.defaultTemplates
import org.junit.Assert.*
import org.junit.Test

class TemplateStoreSerializationTest {

    private val gson = Gson()
    private val listType = object : TypeToken<List<EventTemplateDto>>() {}.type

    private fun roundTrip(templates: List<EventTemplate>): List<EventTemplate> {
        val json = gson.toJson(templates.map(EventTemplateDto::from))
        val dtos: List<EventTemplateDto> = gson.fromJson(json, listType)
        return dtos.map(EventTemplateDto::toEventTemplate)
    }

    @Test
    fun `defaultTemplates round-trip preserves size`() {
        val result = roundTrip(defaultTemplates)
        assertEquals(defaultTemplates.size, result.size)
    }

    @Test
    fun `defaultTemplates round-trip preserves ids`() {
        val original = defaultTemplates.map { it.id }
        val result   = roundTrip(defaultTemplates).map { it.id }
        assertEquals(original, result)
    }

    @Test
    fun `defaultTemplates round-trip preserves categories`() {
        defaultTemplates.zip(roundTrip(defaultTemplates)).forEach { (orig, restored) ->
            assertEquals(orig.category, restored.category)
        }
    }

    @Test
    fun `defaultTemplates round-trip preserves titleFormat`() {
        defaultTemplates.zip(roundTrip(defaultTemplates)).forEach { (orig, restored) ->
            assertEquals(orig.titleFormat, restored.titleFormat)
        }
    }

    @Test
    fun `single FIXED template with dayOfYear round-trips`() {
        val t = EventTemplate(
            id = "fixed-001",
            name = "정기점검",
            category = TemplateCategory.FIXED,
            titleFormat = "정기점검",
            dayOfYear = 162,
            defaultStartHour = 9,
            defaultDurationMinutes = 0,
        )
        val result = roundTrip(listOf(t)).first()
        assertEquals(t.id, result.id)
        assertEquals(t.dayOfYear, result.dayOfYear)
        assertEquals(t.category, result.category)
        assertEquals("[#162] 정기점검", result.fixedDisplayTitle)
    }

    @Test
    fun `empty list round-trips to empty list`() {
        assertEquals(emptyList<EventTemplate>(), roundTrip(emptyList()))
    }

    @Test
    fun `malformed json returns defaultTemplates`() {
        val bad = "NOT_JSON"
        val result = runCatching {
            val dtos: List<EventTemplateDto> = gson.fromJson(bad, listType)
            dtos.map(EventTemplateDto::toEventTemplate)
        }.getOrElse { defaultTemplates }
        assertEquals(defaultTemplates.size, result.size)
    }

    @Test
    fun `EventTemplateDto from and toEventTemplate are inverse`() {
        val orig = EventTemplate(
            id = "abc",
            name = "연인",
            category = TemplateCategory.APPOINTMENT,
            titleFormat = "연인과 약속",
            dayOfYear = null,
            defaultStartHour = 19,
            defaultDurationMinutes = 120,
        )
        val dto = EventTemplateDto.from(orig)
        assertEquals("APPOINTMENT", dto.category)
        val restored = dto.toEventTemplate()
        assertEquals(orig, restored)
    }
}

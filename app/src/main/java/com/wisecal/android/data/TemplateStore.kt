package com.wisecal.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory
import com.wisecal.android.model.defaultTemplates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.templatesDataStore: DataStore<Preferences> by preferencesDataStore(name = "templates")

/** EventTemplate Gson 직렬화용 DTO (enum을 문자열로). */
internal data class EventTemplateDto(
    val id: String,
    val name: String,
    val category: String,
    val titleFormat: String,
    val dayOfYear: Int?,
    val defaultStartHour: Int,
    val defaultDurationMinutes: Int,
) {
    fun toEventTemplate() = EventTemplate(
        id = id,
        name = name,
        category = TemplateCategory.fromString(category),
        titleFormat = titleFormat,
        dayOfYear = dayOfYear,
        defaultStartHour = defaultStartHour,
        defaultDurationMinutes = defaultDurationMinutes,
    )

    companion object {
        fun from(t: EventTemplate) = EventTemplateDto(
            id = t.id,
            name = t.name,
            category = t.category.name,
            titleFormat = t.titleFormat,
            dayOfYear = t.dayOfYear,
            defaultStartHour = t.defaultStartHour,
            defaultDurationMinutes = t.defaultDurationMinutes,
        )
    }
}

class TemplateStore(private val dataStore: DataStore<Preferences>) {

    private val gson = Gson()
    private val KEY = stringPreferencesKey("templates.v1")
    private val listType = object : TypeToken<List<EventTemplateDto>>() {}.type

    val templates: Flow<List<EventTemplate>> = dataStore.data.map { prefs ->
        prefs[KEY]?.let { json ->
            runCatching {
                val dtos: List<EventTemplateDto> = gson.fromJson(json, listType)
                dtos.map(EventTemplateDto::toEventTemplate)
            }.getOrElse { defaultTemplates }
        } ?: defaultTemplates
    }

    suspend fun save(templates: List<EventTemplate>) {
        val json = gson.toJson(templates.map(EventTemplateDto::from))
        dataStore.edit { it[KEY] = json }
    }

    /** 템플릿 추가/교체 (id 기준). */
    suspend fun upsert(template: EventTemplate, current: List<EventTemplate>) {
        val updated = current.filter { it.id != template.id } + template
        save(updated)
    }

    /** 템플릿 삭제. */
    suspend fun delete(id: String, current: List<EventTemplate>) {
        save(current.filter { it.id != id })
    }
}

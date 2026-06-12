package com.wisecal.android.ui.template

import com.wisecal.android.model.EventTemplate
import com.wisecal.android.model.TemplateCategory

/**
 * 템플릿 목록 UI 상태.
 * iOS EventTemplateView 대응. DataStore에서 읽은 EventTemplate 목록 + 카테고리 필터.
 */
data class TemplateState(
    val templates: List<EventTemplate> = emptyList(),
    val selectedCategory: TemplateCategory? = null,
) {
    val filteredTemplates: List<EventTemplate>
        get() = if (selectedCategory == null) templates
                else templates.filter { it.category == selectedCategory }

    val hasTemplates: Boolean get() = templates.isNotEmpty()

    val categoryGroups: Map<TemplateCategory, List<EventTemplate>>
        get() = templates.groupBy { it.category }
}

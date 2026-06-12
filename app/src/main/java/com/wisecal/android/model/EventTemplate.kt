package com.wisecal.android.model

import com.wisecal.android.logic.PlannerLogic
import java.util.UUID

/**
 * 개인 이벤트 입력 템플릿.
 * iOS EventTemplate.swift 대응.
 *
 * @param dayOfYear FIXED 전용. 연중 일련일(1–366). 매년 같은 날 재현.
 * @param defaultStartHour 이벤트 기본 시작 시(0–23).
 * @param defaultDurationMinutes 기본 종료까지 분. 0 = 시작=종료.
 */
data class EventTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val category: TemplateCategory,
    val titleFormat: String,
    val dayOfYear: Int? = null,
    val defaultStartHour: Int = 9,
    val defaultDurationMinutes: Int = 60,
) {
    /** FIXED + dayOfYear 있을 때 `[#162] 제목` 형태 생성. */
    val fixedDisplayTitle: String?
        get() = if (category == TemplateCategory.FIXED && dayOfYear != null)
            PlannerLogic.fixedTitle(dayOfYear, titleFormat)
        else null
}

/** 기본 시드 템플릿 (iOS TemplateStore.seed 대응). */
val defaultTemplates: List<EventTemplate> = listOf(
    // 복용 — 날짜 패턴 없음 → 저장 시 auto yearly RRULE
    EventTemplate(name = "아침 복용",  category = TemplateCategory.MEDICATION, titleFormat = "아침 약 복용",  defaultStartHour = 7,  defaultDurationMinutes = 0),
    EventTemplate(name = "점심 복용",  category = TemplateCategory.MEDICATION, titleFormat = "점심 약 복용",  defaultStartHour = 12, defaultDurationMinutes = 0),
    EventTemplate(name = "저녁 복용",  category = TemplateCategory.MEDICATION, titleFormat = "저녁 약 복용",  defaultStartHour = 18, defaultDurationMinutes = 0),
    // 복용 — d### 패턴: 저장일 연중 일련일 치환
    EventTemplate(name = "알레르기 약(일련일)", category = TemplateCategory.MEDICATION, titleFormat = "알레르기 약 복용 (d###)", defaultStartHour = 9, defaultDurationMinutes = 0),
    // 복용 — yyyy-mm-dd 패턴
    EventTemplate(name = "복용 날짜 기록", category = TemplateCategory.MEDICATION, titleFormat = "약 복용 yyyy-mm-dd", defaultStartHour = 9, defaultDurationMinutes = 0),
    // 약속
    EventTemplate(name = "연인", category = TemplateCategory.APPOINTMENT, titleFormat = "연인과 약속", defaultStartHour = 19, defaultDurationMinutes = 120),
    EventTemplate(name = "친구", category = TemplateCategory.APPOINTMENT, titleFormat = "친구와 약속", defaultStartHour = 14, defaultDurationMinutes = 120),
    EventTemplate(name = "회식", category = TemplateCategory.APPOINTMENT, titleFormat = "회식",       defaultStartHour = 18, defaultDurationMinutes = 180),
    // 고정 — 패턴 조합
    EventTemplate(name = "정기작업", category = TemplateCategory.FIXED, titleFormat = "[정기작업] (d###) yyyy-mm-dd", defaultStartHour = 9, defaultDurationMinutes = 0),
)

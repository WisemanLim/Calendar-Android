package com.wisecal.android.logic

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 일기 모드 이벤트 기본 제목 생성.
 * PRD §3.4-A: `[yyyy.MM.dd]{emoji}수고했어요.`
 * 이모지: 0~1개=😀, 2~3개=😊, 4+개=🤯
 */
object DiaryTitleBuilder {

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd")

    fun defaultTitle(date: LocalDate, eventCount: Int): String {
        val emoji = emojiFor(eventCount)
        return "[${date.format(DATE_FORMATTER)}]${emoji}수고했어요."
    }

    fun emojiFor(eventCount: Int): String = when {
        eventCount <= 1 -> "😀"
        eventCount <= 3 -> "😊"
        else -> "🤯"
    }
}

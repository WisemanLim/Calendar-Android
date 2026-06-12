package com.wisecal.android.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class DiaryTitleBuilderTest {

    private val date = LocalDate.of(2026, 6, 11)

    // ── 이모지 규칙 ────────────────────────────────────────────

    @Test
    fun `emojiFor 0 events returns happy`() {
        assertEquals("😀", DiaryTitleBuilder.emojiFor(0))
    }

    @Test
    fun `emojiFor 1 event returns happy`() {
        assertEquals("😀", DiaryTitleBuilder.emojiFor(1))
    }

    @Test
    fun `emojiFor 2 events returns smile`() {
        assertEquals("😊", DiaryTitleBuilder.emojiFor(2))
    }

    @Test
    fun `emojiFor 3 events returns smile`() {
        assertEquals("😊", DiaryTitleBuilder.emojiFor(3))
    }

    @Test
    fun `emojiFor 4 events returns mindblown`() {
        assertEquals("🤯", DiaryTitleBuilder.emojiFor(4))
    }

    @Test
    fun `emojiFor large count returns mindblown`() {
        assertEquals("🤯", DiaryTitleBuilder.emojiFor(100))
    }

    // ── defaultTitle 포맷 ──────────────────────────────────────

    @Test
    fun `defaultTitle contains formatted date`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 0)
        assertTrue(title.contains("2026.06.11"))
    }

    @Test
    fun `defaultTitle starts with bracket date`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 0)
        assertTrue(title.startsWith("[2026.06.11]"))
    }

    @Test
    fun `defaultTitle ends with closing phrase`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 0)
        assertTrue(title.endsWith("수고했어요."))
    }

    @Test
    fun `defaultTitle emoji immediately after bracket no space`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 0)
        // 형식: [yyyy.MM.dd]{emoji}수고했어요.  — 공백 없음
        assertTrue(title.matches(Regex("\\[\\d{4}\\.\\d{2}\\.\\d{2}].+수고했어요\\."))  )
    }

    @Test
    fun `defaultTitle 0 events full string`() {
        val title = DiaryTitleBuilder.defaultTitle(LocalDate.of(2026, 1, 5), 0)
        assertEquals("[2026.01.05]😀수고했어요.", title)
    }

    @Test
    fun `defaultTitle 2 events includes smile emoji`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 2)
        assertTrue(title.contains("😊"))
    }

    @Test
    fun `defaultTitle 5 events includes mindblown emoji`() {
        val title = DiaryTitleBuilder.defaultTitle(date, 5)
        assertTrue(title.contains("🤯"))
    }
}

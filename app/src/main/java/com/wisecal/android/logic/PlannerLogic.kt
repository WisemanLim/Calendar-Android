package com.wisecal.android.logic

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

/**
 * 플래너 모드/템플릿 순수 로직 — 뷰 비의존, 테스트 가능.
 * iOS PlannerLogic.swift 대응.
 */
object PlannerLogic {

    // ──────────────────────────────────────────────
    // day-of-year
    // ──────────────────────────────────────────────

    /** 연중 일련일 (1 = 1월 1일). */
    fun dayOfYear(date: LocalDate): Int = date.get(ChronoField.DAY_OF_YEAR)

    // ──────────────────────────────────────────────
    // 고정 이벤트 표기 [#ddd] 제목
    // ──────────────────────────────────────────────

    /** `[#162] 알레르기 약 복용` 형태 생성. */
    fun fixedTitle(day: Int, title: String): String = "[#$day] $title"

    /** `[#ddd] 제목` 파싱 → Pair(day, title). 형식 불일치 시 null. */
    fun parseFixedTitle(raw: String): Pair<Int, String>? {
        if (!raw.startsWith("[#")) return null
        val close = raw.indexOf(']').takeIf { it > 0 } ?: return null
        val day = raw.substring(2, close).toIntOrNull() ?: return null
        val title = raw.substring(close + 1).trim()
        return Pair(day, title)
    }

    // ──────────────────────────────────────────────
    // 투두 인코딩 (CalendarContract.Events → notes)
    // ──────────────────────────────────────────────

    data class TodoItem(val text: String, val done: Boolean)

    /** 체크리스트 → notes 문자열. `- [ ] 항목` / `- [x] 항목`. */
    fun encodeTodos(items: List<TodoItem>): String =
        items.joinToString("\n") { "- [${if (it.done) "x" else " "}] ${it.text}" }

    /** notes 문자열 → 체크리스트. 체크박스 라인만 파싱. */
    fun decodeTodos(notes: String): List<TodoItem> =
        notes.lines()
            .filter { it.trimStart().startsWith("- [") && it.length >= 5 }
            .mapNotNull { line ->
                val s = line.trimStart()
                if (s.length < 6) return@mapNotNull null
                val mark = s[3]
                val done = mark == 'x' || mark == 'X'
                val text = s.substringAfter("] ").trim()
                if (text.isEmpty()) null else TodoItem(text, done)
            }

    // ──────────────────────────────────────────────
    // 타이틀 날짜 패턴 치환
    // ──────────────────────────────────────────────

    private val PATTERNS = listOf(
        "yyyy-mm-dd", "yyyy/mm/dd", "yy-mm-dd",
        "yyyy.mm.dd", "dd-mm-yyyy", "d###"
    )

    /**
     * 타이틀의 날짜 패턴을 실제 날짜 문자열로 치환.
     * yyyy 계열 먼저 처리해 yy-mm-dd 와 충돌 방지.
     */
    fun substituteTitlePatterns(title: String, date: LocalDate): String {
        val yyyy = String.format("%04d", date.year)
        val yy   = String.format("%02d", date.year % 100)
        val mm   = String.format("%02d", date.monthValue)
        val dd   = String.format("%02d", date.dayOfMonth)
        val ddd  = String.format("%03d", dayOfYear(date))
        return title
            .replace("yyyy-mm-dd", "$yyyy-$mm-$dd")
            .replace("yyyy/mm/dd", "$yyyy/$mm/$dd")
            .replace("yy-mm-dd",   "$yy-$mm-$dd")
            .replace("yyyy.mm.dd", "$yyyy.$mm.$dd")
            .replace("dd-mm-yyyy", "$dd-$mm-$yyyy")
            .replace("d###",       ddd)
    }

    /** 타이틀에 지원 날짜 패턴 포함 여부. */
    fun hasTitleDatePattern(title: String): Boolean =
        PATTERNS.any { title.contains(it) }
}

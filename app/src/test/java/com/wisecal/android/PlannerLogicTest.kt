package com.wisecal.android

import com.wisecal.android.logic.PlannerLogic
import com.wisecal.android.logic.PlannerLogic.TodoItem
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class PlannerLogicTest {

    // ── day-of-year ──────────────────────────────

    @Test
    fun `dayOfYear Jan 1 = 1`() {
        assertEquals(1, PlannerLogic.dayOfYear(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `dayOfYear Dec 31 non-leap = 365`() {
        assertEquals(365, PlannerLogic.dayOfYear(LocalDate.of(2026, 12, 31)))
    }

    @Test
    fun `dayOfYear Dec 31 leap = 366`() {
        assertEquals(366, PlannerLogic.dayOfYear(LocalDate.of(2024, 12, 31)))
    }

    @Test
    fun `dayOfYear Mar 8 2026 = 67`() {
        assertEquals(67, PlannerLogic.dayOfYear(LocalDate.of(2026, 3, 8)))
    }

    // ── fixedTitle / parseFixedTitle ─────────────

    @Test
    fun `fixedTitle formats correctly`() {
        assertEquals("[#162] 알레르기 약 복용", PlannerLogic.fixedTitle(162, "알레르기 약 복용"))
    }

    @Test
    fun `parseFixedTitle round trips`() {
        val result = PlannerLogic.parseFixedTitle("[#162] 알레르기 약 복용")
        assertNotNull(result)
        assertEquals(162, result!!.first)
        assertEquals("알레르기 약 복용", result.second)
    }

    @Test
    fun `parseFixedTitle returns null for invalid input`() {
        assertNull(PlannerLogic.parseFixedTitle("알레르기 약 복용"))
        assertNull(PlannerLogic.parseFixedTitle("[123] 제목"))
        assertNull(PlannerLogic.parseFixedTitle("[#abc] 제목"))
    }

    // ── todo encode / decode ─────────────────────

    @Test
    fun `encodeTodos produces markdown checklist`() {
        val items = listOf(TodoItem("운동", false), TodoItem("독서", true))
        val encoded = PlannerLogic.encodeTodos(items)
        assertEquals("- [ ] 운동\n- [x] 독서", encoded)
    }

    @Test
    fun `decodeTodos parses markdown checklist`() {
        val notes = "- [ ] 운동\n- [x] 독서\n일반 메모 라인"
        val items = PlannerLogic.decodeTodos(notes)
        assertEquals(2, items.size)
        assertEquals(TodoItem("운동", false), items[0])
        assertEquals(TodoItem("독서", true), items[1])
    }

    @Test
    fun `encodeDecode round trip`() {
        val original = listOf(
            TodoItem("항목A", false),
            TodoItem("항목B", true),
            TodoItem("항목C", false),
        )
        val decoded = PlannerLogic.decodeTodos(PlannerLogic.encodeTodos(original))
        assertEquals(original, decoded)
    }

    // ── substituteTitlePatterns ──────────────────

    private val date = LocalDate.of(2026, 3, 8) // day-of-year = 67

    @Test
    fun `substitute yyyy-mm-dd`() {
        assertEquals("약 복용 2026-03-08", PlannerLogic.substituteTitlePatterns("약 복용 yyyy-mm-dd", date))
    }

    @Test
    fun `substitute yyyy_slash_mm_slash_dd`() {
        assertEquals("2026/03/08", PlannerLogic.substituteTitlePatterns("yyyy/mm/dd", date))
    }

    @Test
    fun `substitute yy-mm-dd`() {
        assertEquals("26-03-08", PlannerLogic.substituteTitlePatterns("yy-mm-dd", date))
    }

    @Test
    fun `substitute yyyy dot mm dot dd`() {
        assertEquals("2026.03.08", PlannerLogic.substituteTitlePatterns("yyyy.mm.dd", date))
    }

    @Test
    fun `substitute dd-mm-yyyy`() {
        assertEquals("08-03-2026", PlannerLogic.substituteTitlePatterns("dd-mm-yyyy", date))
    }

    @Test
    fun `substitute d### with 3-digit day-of-year`() {
        assertEquals("067", PlannerLogic.substituteTitlePatterns("d###", date))
    }

    @Test
    fun `substitute combined pattern`() {
        val result = PlannerLogic.substituteTitlePatterns("[정기작업] (d###) yyyy-mm-dd", date)
        assertEquals("[정기작업] (067) 2026-03-08", result)
    }

    @Test
    fun `no pattern returns title unchanged`() {
        assertEquals("일반 제목", PlannerLogic.substituteTitlePatterns("일반 제목", date))
    }

    // ── hasTitleDatePattern ──────────────────────

    @Test
    fun `hasTitleDatePattern detects patterns`() {
        assertTrue(PlannerLogic.hasTitleDatePattern("약 복용 yyyy-mm-dd"))
        assertTrue(PlannerLogic.hasTitleDatePattern("d###"))
        assertTrue(PlannerLogic.hasTitleDatePattern("날짜: yyyy/mm/dd"))
    }

    @Test
    fun `hasTitleDatePattern returns false for plain title`() {
        assertFalse(PlannerLogic.hasTitleDatePattern("알레르기 약 복용"))
        assertFalse(PlannerLogic.hasTitleDatePattern(""))
    }
}

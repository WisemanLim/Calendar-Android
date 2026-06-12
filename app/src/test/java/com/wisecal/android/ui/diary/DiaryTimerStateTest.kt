package com.wisecal.android.ui.diary

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime

class DiaryTimerStateTest {

    private val base = LocalDateTime.of(2026, 6, 11, 9, 0, 0)

    // ── 초기 상태 ──────────────────────────────────────────────────

    @Test fun `initial elapsedSeconds is 0`() {
        assertEquals(0L, DiaryTimerState(base).elapsedSeconds)
    }

    // ── displayTime mm:ss 포맷 ─────────────────────────────────────

    @Test fun `displayTime 00-00 at 0 seconds`() {
        assertEquals("00:00", DiaryTimerState(base, 0L).displayTime)
    }

    @Test fun `displayTime 00-01 at 1 second`() {
        assertEquals("00:01", DiaryTimerState(base, 1L).displayTime)
    }

    @Test fun `displayTime 01-30 at 90 seconds`() {
        assertEquals("01:30", DiaryTimerState(base, 90L).displayTime)
    }

    @Test fun `displayTime 59-59 at 3599 seconds`() {
        assertEquals("59:59", DiaryTimerState(base, 3599L).displayTime)
    }

    @Test fun `displayTime leading zeros in minutes and seconds`() {
        // 65초 → 01:05
        assertEquals("01:05", DiaryTimerState(base, 65L).displayTime)
    }

    // ── displayTime h:mm:ss 포맷 (3600초 이상) ─────────────────────

    @Test fun `displayTime switches to h-mm-ss at 3600 seconds`() {
        assertEquals("1:00:00", DiaryTimerState(base, 3600L).displayTime)
    }

    @Test fun `displayTime 1-01-05 at 3665 seconds`() {
        assertEquals("1:01:05", DiaryTimerState(base, 3665L).displayTime)
    }

    @Test fun `displayTime hours has no leading zero`() {
        // 3601초 → "1:00:01" not "01:00:01"
        val d = DiaryTimerState(base, 3601L).displayTime
        assertFalse(d.startsWith("0"))
    }

    @Test fun `displayTime 10-00-00 at 36000 seconds`() {
        assertEquals("10:00:00", DiaryTimerState(base, 36000L).displayTime)
    }

    // ── endTime ────────────────────────────────────────────────────

    @Test fun `endTime equals startedAt when elapsedSeconds 0`() {
        assertEquals(base, DiaryTimerState(base, 0L).endTime)
    }

    @Test fun `endTime is startedAt plus elapsed minutes`() {
        val s = DiaryTimerState(base, 60L)
        assertEquals(base.plusSeconds(60), s.endTime)
    }

    @Test fun `endTime is startedAt plus 1 hour at 3600 seconds`() {
        val s = DiaryTimerState(base, 3600L)
        assertEquals(base.plusHours(1), s.endTime)
    }
}

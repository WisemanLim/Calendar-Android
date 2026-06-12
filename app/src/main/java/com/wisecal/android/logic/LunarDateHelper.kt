package com.wisecal.android.logic

import android.icu.util.ChineseCalendar
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

/** 양력 → 음력 변환. android.icu.util.ChineseCalendar 사용 (API 24+). */
object LunarDateHelper {
    fun toLunarString(date: LocalDate): String {
        val cal = ChineseCalendar()
        cal.time = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())
        val month = cal.get(ChineseCalendar.MONTH) + 1
        val day = cal.get(ChineseCalendar.DAY_OF_MONTH)
        return "음력 ${month}월 ${day}일"
    }
}

package com.wisecal.android.ui.theme

import androidx.compose.ui.graphics.Color

/** 레인보우 7단계 이벤트 수 색상 (iOS CalendarView rainbow 대응). */
object EventCountColor {
    val None   = Color.Transparent
    val One    = Color(0xFFFFCC00) // 노랑
    val Two    = Color(0xFFFF6600) // 주황
    val Three  = Color(0xFF00CC44) // 초록
    val Four   = Color(0xFF003399) // 네이비
    val Five   = Color(0xFF9933CC) // 보라
    val SixPlus = Color(0xFFCC0000) // 빨강

    fun forCount(count: Int): Color = when (count) {
        0    -> None
        1    -> One
        2    -> Two
        3    -> Three
        4    -> Four
        5    -> Five
        else -> SixPlus
    }
}

package com.wisecal.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisecal.android.logic.DiaryTitleBuilder
import com.wisecal.android.model.WeatherInfo
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale

private val SundayRed = Color(0xFFE03131)

/**
 * 주간 스트립 — 선택일 기준 일요일~토요일 7칸.
 * iOS CalendarView weekStrip 대응: 일=빨강, 토=파랑(primary), 선택일=원형 강조.
 */
@Composable
fun WeekStrip(
    selectedDate: LocalDate,
    eventCountByDate: Map<String, Int>,
    onDateClick: (LocalDate) -> Unit,
    weatherByDate: Map<LocalDate, WeatherInfo> = emptyMap(),
    modifier: Modifier = Modifier,
) {
    val weekStart = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (i in 0..6) {
            val date = weekStart.plusDays(i.toLong())
            val isSelected = date == selectedDate
            val count = eventCountByDate[date.toString()] ?: 0
            val emoji = weatherByDate[date]?.iconEmoji ?: DiaryTitleBuilder.emojiFor(count)
            val isSunday = i == 0
            val isSaturday = i == 6

            val nameColor = when {
                isSunday   -> SundayRed
                isSaturday -> MaterialTheme.colorScheme.primary
                else       -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            val numColor = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                isSunday   -> SundayRed
                isSaturday -> MaterialTheme.colorScheme.primary
                else       -> MaterialTheme.colorScheme.onSurface
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDateClick(date) }
                    .padding(vertical = 4.dp),
            ) {
                // 요일명
                Text(
                    text = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.KOREAN),
                    fontSize = 11.sp,
                    color = nameColor,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(3.dp))
                // 날짜 숫자 — 선택 시만 원형 강조
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = date.dayOfMonth.toString(),
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = numColor,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                // 날씨 이모티콘
                Text(
                    text = emoji,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

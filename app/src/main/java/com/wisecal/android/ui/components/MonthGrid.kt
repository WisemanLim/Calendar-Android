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
import com.wisecal.android.logic.DayInfo
import com.wisecal.android.ui.theme.EventCountColor
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SundayRed = Color(0xFFE03131)
private val DAY_HEADERS = listOf("일", "월", "화", "수", "목", "금", "토")
private val MONTH_FORMATTER = DateTimeFormatter.ofPattern("M월", Locale.KOREAN)

/**
 * 6×7 월 그리드.
 * iOS CalendarView monthGrid 대응.
 * 일=빨강, 토=파랑(primary). 이벤트 수 dot 색상(레인보우).
 */
@Composable
fun MonthGrid(
    grid: List<DayInfo>,
    selectedDate: LocalDate,
    today: LocalDate = LocalDate.now(),
    yearMonth: YearMonth? = null,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 4.dp, vertical = 4.dp)) {
        // 월 헤더
        if (yearMonth != null) {
            Text(
                text = yearMonth.format(MONTH_FORMATTER),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
            )
        }

        // 요일 헤더 (일~토, 일=빨강, 토=파랑)
        Row(modifier = Modifier.fillMaxWidth()) {
            DAY_HEADERS.forEachIndexed { index, label ->
                val headerColor = when (index) {
                    0    -> SundayRed
                    6    -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    color = headerColor,
                )
            }
        }
        Spacer(modifier = Modifier.height(2.dp))

        // 42칸 = 6행 × 7열
        grid.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    DayCell(
                        day = day,
                        isSelected = day.date == selectedDate,
                        isToday = day.date == today,
                        onDateClick = onDateClick,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: DayInfo,
    isSelected: Boolean,
    isToday: Boolean,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSunday   = day.date.dayOfWeek == DayOfWeek.SUNDAY
    val isSaturday = day.date.dayOfWeek == DayOfWeek.SATURDAY

    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday    -> MaterialTheme.colorScheme.primaryContainer
        else       -> Color.Transparent
    }
    val textColor = when {
        isSelected         -> MaterialTheme.colorScheme.onPrimary
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        isSunday           -> SundayRed
        isSaturday         -> MaterialTheme.colorScheme.primary
        isToday            -> MaterialTheme.colorScheme.primary
        else               -> MaterialTheme.colorScheme.onSurface
    }
    val dotColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                   else EventCountColor.forCount(day.eventCount)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onDateClick(day.date) }
            .padding(2.dp),
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            fontSize = 11.sp,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (day.eventCount > 0) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(dotColor),
            )
        }
    }
}

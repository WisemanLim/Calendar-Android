package com.wisecal.android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisecal.android.model.WeatherInfo
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val monthFormatter = DateTimeFormatter.ofPattern("yyyy년 M월", Locale.KOREAN)
private val dayFormatter   = DateTimeFormatter.ofPattern("d일 E", Locale.KOREAN)

/**
 * 히어로 날짜 바 — iOS CalendarView 상단 날짜 + 날씨 대응.
 * @param onMonthClick 월 표시 탭 → 월 이동 UI 트리거.
 */
@Composable
fun HeroDateBar(
    selectedDate: LocalDate,
    weather: WeatherInfo?,
    onMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = selectedDate.format(monthFormatter),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onMonthClick() },
            )
            Text(
                text = selectedDate.format(dayFormatter),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        if (weather != null) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = weather.iconEmoji, fontSize = 24.sp)
                Text(
                    text = weather.displayText,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

package com.wisecal.android.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.wisecal.android.logic.LunarDateHelper
import com.wisecal.android.logic.OverdueChecker
import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.model.PlannerMode
import com.wisecal.android.ui.components.MonthGrid
import com.wisecal.android.ui.components.WeekStrip
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * 메인 캘린더 화면.
 * iOS WiseCal 메인화면 대응.
 * 레이아웃: 유틸리티 바 → 월 헤더 → 주간 스트립 → Hero 카드(weight=1) → 미니 달력 카드(하단 고정)
 */
@Composable
fun MainScreen(
    viewModel: CalendarViewModel,
    onNavigateToSearch: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onAddEvent: (LocalDate) -> Unit = {},
    onCopyEvent: (CalendarEvent) -> Unit = {},
    onEditEvent: (CalendarEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val state by viewModel.uiState.collectAsState()
    val today = LocalDate.now()
    var heroDrag by remember { mutableFloatStateOf(0f) }

    // 위치 권한 → 날씨 로드
    val context = LocalContext.current
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                locationClient.lastLocation.addOnSuccessListener { loc ->
                    viewModel.loadWeather(loc?.latitude ?: 37.5665, loc?.longitude ?: 126.9780)
                }
            } catch (_: SecurityException) {
                viewModel.loadWeather(37.5665, 126.9780)
            }
        } else {
            viewModel.loadWeather(37.5665, 126.9780)
        }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                locationClient.lastLocation.addOnSuccessListener { loc ->
                    viewModel.loadWeather(loc?.latitude ?: 37.5665, loc?.longitude ?: 126.9780)
                }
            } catch (_: SecurityException) {
                viewModel.loadWeather(37.5665, 126.9780)
            }
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    // 엣지투엣지(enableEdgeToEdge) 대응: 시스템 바(상단 상태바 / 하단 제스처바) 영역만큼
    // 안전 패딩을 적용해 설정 버튼·하단 미니달력이 시스템 UI에 가려지지 않도록 방어.
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
    ) {

        // ── 유틸리티 바 ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "설정")
            }
            Spacer(modifier = Modifier.weight(1f))
            if (state.selectedDate != today || state.currentMonth != java.time.YearMonth.from(today)) {
                TextButton(
                    onClick = { viewModel.selectDate(today) },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                ) {
                    Text("Today", style = MaterialTheme.typography.labelMedium)
                }
            }
            // 연도 네비게이션
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp),
            ) {
                IconButton(
                    onClick = { viewModel.navigateMonth(-12) },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "이전 연도",
                        modifier = Modifier.size(16.dp),
                    )
                }
                Text(
                    text = "${state.currentMonth.year}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                IconButton(
                    onClick = { viewModel.navigateMonth(12) },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "다음 연도",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onNavigateToSearch) {
                Icon(Icons.Default.Search, contentDescription = "검색")
            }
            IconButton(onClick = { onAddEvent(state.selectedDate) }) {
                Icon(Icons.Default.Add, contentDescription = "추가")
            }
        }

        // ── 월 헤더 ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { viewModel.navigateMonth(-1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "이전 달")
            }
            Text(
                text = state.currentMonth
                    .format(DateTimeFormatter.ofPattern("M월 yyyy", Locale.KOREAN))
                    .uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { viewModel.navigateMonth(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "다음 달")
            }
        }

        // ── 주간 스트립 ────────────────────────────────────────────
        WeekStrip(
            selectedDate = state.selectedDate,
            eventCountByDate = state.eventsForMonth.mapValues { it.value.size },
            onDateClick = viewModel::selectDate,
            weatherByDate = state.weekWeatherMap,
        )

        // ── Hero 카드 (남은 공간 모두 차지) ──────────────────────────
        HeroArea(
            state = state,
            onAddEvent = { onAddEvent(state.selectedDate) },
            onCopyEvent = onCopyEvent,
            onEditEvent = onEditEvent,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(state.selectedDate) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (heroDrag < -80f) viewModel.selectDate(state.selectedDate.plusDays(1))
                            else if (heroDrag > 80f) viewModel.selectDate(state.selectedDate.minusDays(1))
                            heroDrag = 0f
                        },
                        onHorizontalDrag = { _, delta -> heroDrag += delta },
                    )
                },
        )

        // ── 하단 미니 달력 카드 (하단 고정) ──────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            shape = RoundedCornerShape(16.dp),
            tonalElevation = 1.dp,
            shadowElevation = 1.dp,
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonthGrid(
                    grid = state.monthGrid,
                    selectedDate = state.selectedDate,
                    yearMonth = state.currentMonth,
                    onDateClick = viewModel::selectDate,
                    modifier = Modifier.weight(1f),
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .padding(vertical = 8.dp),
                    thickness = 0.5.dp,
                )
                MonthGrid(
                    grid = state.nextMonthGrid,
                    selectedDate = state.selectedDate,
                    yearMonth = state.currentMonth.plusMonths(1),
                    onDateClick = { date ->
                        viewModel.selectDate(date)
                        viewModel.navigateMonth(1)
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// ── Hero Area ───────────────────────────────────────────────────────────────

@Composable
private fun HeroArea(
    state: CalendarUiState,
    onAddEvent: () -> Unit,
    onCopyEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val now = LocalDateTime.now()
    val lunarStr = remember(state.selectedDate) {
        runCatching { LunarDateHelper.toLunarString(state.selectedDate) }.getOrElse { "" }
    }
    val dayName = state.selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)

    Surface(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // 방어 코드: Hero 카드에 실제로 주어진 가용 크기에 맞춰 날짜 숫자 크기를 동적 조정.
            // 작은 화면/짧은 세로 공간에서 180sp 고정값이 레이아웃을 밀어내는 것을 방지.
            // min(가로,세로) 기준 비율 → 72~180sp 범위로 클램프.
            val dateFontSize = (minOf(maxWidth, maxHeight).value * 0.55f)
                .coerceIn(72f, 180f).sp

            // 날짜 숫자 워터마크 — 항상 연한 회색 (iOS와 동일)
            Text(
                text = state.selectedDate.dayOfMonth.toString(),
                fontSize = dateFontSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f),
                modifier = Modifier.align(Alignment.Center),
                lineHeight = dateFontSize,
            )

            // 전경 레이아웃
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                // 음력 날짜 + 날씨 (같은 라인)
                val dayWeather = state.weekWeatherMap[state.selectedDate] ?: state.weather
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (lunarStr.isNotEmpty()) {
                        Text(
                            text = lunarStr,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (dayWeather != null) {
                        Text(text = dayWeather.iconEmoji, fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = dayWeather.displayText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                // 이벤트 목록 (중간, 남은 공간)
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    if (state.eventsForSelectedDate.isEmpty()) {
                        item {
                            // 이벤트 없을 때 조용히 비워둠 (워터마크가 채움)
                        }
                    } else {
                        items(state.eventsForSelectedDate, key = { it.id }) { event ->
                            EventRow(
                                event = event,
                                plannerMode = state.plannerMode,
                                now = now,
                                onCopyEvent = onCopyEvent,
                                onEditEvent = onEditEvent,
                            )
                            HorizontalDivider(thickness = 0.5.dp)
                        }
                    }
                }

                // 요일 이름 (최하단 가운데)
                Text(
                    text = dayName.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    letterSpacing = 4.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                )
            }
        }
    }
}

// ── Event Row ────────────────────────────────────────────────────────────────

private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.KOREAN)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun EventRow(
    event: CalendarEvent,
    plannerMode: PlannerMode,
    now: LocalDateTime,
    onCopyEvent: (CalendarEvent) -> Unit,
    onEditEvent: (CalendarEvent) -> Unit,
) {
    val isOverdue = OverdueChecker.isOverdue(event, plannerMode, now)
    val titleColor = if (isOverdue) MaterialTheme.colorScheme.error
                     else MaterialTheme.colorScheme.onSurface
    val timeColor = if (isOverdue) MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.onSurfaceVariant

    var menuExpanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onEditEvent(event) },
                    onLongClick = { menuExpanded = true },
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!event.isAllDay) {
                Text(
                    text = event.startTime.format(timeFormatter),
                    style = MaterialTheme.typography.labelSmall,
                    color = timeColor,
                )
            }
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                color = titleColor,
                modifier = Modifier.weight(1f),
            )
        }

        DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
            DropdownMenuItem(
                text = { Text("복사 (내용 포함)") },
                onClick = { menuExpanded = false; onCopyEvent(event) },
            )
            DropdownMenuItem(
                text = { Text("복사 (내용 제외)") },
                onClick = { menuExpanded = false; onCopyEvent(event.copy(notes = null)) },
            )
            DropdownMenuItem(
                text = { Text("삭제", color = MaterialTheme.colorScheme.error) },
                onClick = { menuExpanded = false },
            )
        }
    }
}

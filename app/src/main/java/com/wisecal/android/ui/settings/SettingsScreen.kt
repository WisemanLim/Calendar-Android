package com.wisecal.android.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wisecal.android.data.AppSettings
import com.wisecal.android.model.AppColorScheme
import com.wisecal.android.model.PlannerMode
import kotlinx.coroutines.launch

/**
 * 설정 화면.
 * iOS AppSettings 대응. DataStore Preferences 6개 키 편집.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    appSettings: AppSettings,
    settingsViewModel: SettingsViewModel? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val plannerMode     by appSettings.plannerMode.collectAsState(initial = PlannerMode.TODO)
    val notifications   by appSettings.notificationsEnabled.collectAsState(initial = false)
    val colorScheme     by appSettings.appColorScheme.collectAsState(initial = AppColorScheme.SYSTEM)
    val filterState     by (settingsViewModel?.filterState ?: kotlinx.coroutines.flow.MutableStateFlow(CalendarFilterState())).collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("설정") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로")
                    }
                },
            )
        },
        modifier = modifier,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            // ── 플래너 모드 ────────────────────────────────────────
            item {
                SectionHeader("플래너 모드")
                PlannerMode.entries.forEach { mode ->
                    SettingsRadioRow(
                        label = mode.title,
                        description = mode.detail,
                        selected = plannerMode == mode,
                        onClick = { scope.launch { appSettings.setPlannerMode(mode) } },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // ── 컬러 테마 ──────────────────────────────────────────
            item {
                SectionHeader("테마")
                AppColorScheme.entries.forEach { scheme ->
                    SettingsRadioRow(
                        label = scheme.label,
                        description = null,
                        selected = colorScheme == scheme,
                        onClick = { scope.launch { appSettings.setAppColorScheme(scheme) } },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // ── 알림 ───────────────────────────────────────────────
            item {
                SectionHeader("알림")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("이벤트 알림", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = notifications,
                        onCheckedChange = { v ->
                            scope.launch { appSettings.setNotificationsEnabled(v) }
                        },
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }

            // ── 캘린더 연동 ────────────────────────────────────────
            item { SectionHeader("캘린더 선택") }
            if (filterState.availableCalendars.isEmpty()) {
                item {
                    Text(
                        "캘린더를 불러오는 중이거나 권한이 없습니다.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            } else {
                items(filterState.availableCalendars) { cal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(cal.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                cal.accountName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Checkbox(
                            checked = filterState.isEnabled(cal.id),
                            onCheckedChange = { settingsViewModel?.toggleCalendar(cal.id) },
                        )
                    }
                }
            }

            // ── 고객문의 ───────────────────────────────────────────
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SectionHeader("고객문의")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Column {
                        Text("이메일 문의", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "support@wisecal.app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    Column {
                        Text("앱 버전", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            com.wisecal.android.BuildConfig.VERSION_NAME,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    )
}

@Composable
private fun SettingsRadioRow(
    label: String,
    description: String?,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium)
            if (description != null) {
                Text(description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

package com.wisecal.android.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.wisecal.android.BuildConfig
import com.wisecal.android.data.AppSettings
import com.wisecal.android.data.CalendarRepository
import com.wisecal.android.data.TemplateStore
import com.wisecal.android.data.WeatherRepository
import com.wisecal.android.model.CalendarEvent
import com.wisecal.android.ui.CalendarViewModel
import com.wisecal.android.ui.MainScreen
import com.wisecal.android.ui.copy.EventCopySheet
import com.wisecal.android.ui.copy.EventCopyViewModel
import com.wisecal.android.ui.editor.EventEditorScreen
import com.wisecal.android.ui.editor.EventEditorViewModel
import com.wisecal.android.ui.onboarding.AppVersionState
import com.wisecal.android.ui.onboarding.OnboardingScreen
import com.wisecal.android.ui.onboarding.OnboardingState
import com.wisecal.android.ui.search.SearchScreen
import com.wisecal.android.ui.search.SearchViewModel
import com.wisecal.android.ui.settings.SettingsScreen
import com.wisecal.android.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * 앱 전체 Navigation 그래프.
 * AppVersionState.shouldShowOnboarding 으로 시작 화면 결정:
 *   - DEBUG 빌드: 매회 온보딩 노출
 *   - 최초 설치(onboardingCompleted=false): 온보딩
 *   - 버전 변경: 온보딩
 *   - 그 외: 메인 직행
 */
@Composable
fun WiseCalNavGraph(
    appSettings: AppSettings,
    calendarRepo: CalendarRepository,
    weatherRepo: WeatherRepository,
    templateStore: TemplateStore,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val templates by templateStore.templates.collectAsState(initial = emptyList())

    val onboardingCompleted by appSettings.onboardingCompleted.collectAsState(initial = null)
    val lastSeenVersion    by appSettings.lastSeenVersion.collectAsState(initial = null)

    // DataStore 읽기 완료 대기
    if (onboardingCompleted == null || lastSeenVersion == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val versionState = AppVersionState(
        currentVersionName = BuildConfig.VERSION_NAME,
        lastSeenVersion    = lastSeenVersion!!,
        onboardingCompleted = onboardingCompleted!!,
        isDebugBuild       = BuildConfig.DEBUG,
    )
    val startDest = if (versionState.shouldShowOnboarding) NavRoute.Onboarding.route
                    else NavRoute.Main.route

    // 이벤트 복사 오버레이 상태
    var copyTarget by remember { mutableStateOf<CalendarEvent?>(null) }
    var copyDate   by remember { mutableStateOf(LocalDate.now()) }

    NavHost(
        navController    = navController,
        startDestination = startDest,
        modifier         = modifier,
    ) {

        // ── 메인 ────────────────────────────────────────────────────
        composable(NavRoute.Main.route) {
            val vm: CalendarViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(c: Class<T>): T =
                        CalendarViewModel(calendarRepo, appSettings, weatherRepo) as T
                }
            )
            MainScreen(
                viewModel            = vm,
                onNavigateToSearch   = { navController.navigate(NavRoute.Search.route) },
                onNavigateToSettings = { navController.navigate(NavRoute.Settings.route) },
                onAddEvent           = { date -> navController.navigate(NavRoute.Editor.build(date)) },
                onCopyEvent          = { event ->
                    copyTarget = event
                    copyDate   = event.startTime.toLocalDate()
                },
            )
        }

        // ── 온보딩 ──────────────────────────────────────────────────
        composable(NavRoute.Onboarding.route) {
            var state by remember { mutableStateOf(OnboardingState()) }
            OnboardingScreen(
                state         = state,
                onStateChange = { state = it },
                onComplete    = { mode ->
                    scope.launch {
                        appSettings.setPlannerMode(mode)
                        appSettings.setOnboardingCompleted(true)
                        appSettings.setLastSeenVersion(BuildConfig.VERSION_NAME)
                    }
                    navController.navigate(NavRoute.Main.route) {
                        popUpTo(NavRoute.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }

        // ── 검색 ────────────────────────────────────────────────────
        composable(NavRoute.Search.route) {
            val vm: SearchViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(c: Class<T>): T =
                        SearchViewModel(calendarRepo) as T
                }
            )
            SearchScreen(
                viewModel    = vm,
                onEventClick = { navController.popBackStack() },
                onBack       = { navController.popBackStack() },
            )
        }

        // ── 설정 ────────────────────────────────────────────────────
        composable(NavRoute.Settings.route) {
            val settingsVm: SettingsViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(c: Class<T>): T =
                        SettingsViewModel(calendarRepo, appSettings) as T
                }
            )
            SettingsScreen(
                appSettings       = appSettings,
                settingsViewModel = settingsVm,
                onBack            = { navController.popBackStack() },
            )
        }

        // ── 이벤트 에디터 ───────────────────────────────────────────
        composable(
            route     = NavRoute.Editor.route,
            arguments = listOf(navArgument(NavRoute.Editor.DATE_KEY) { type = NavType.StringType }),
        ) { backStack ->
            val dateStr = backStack.arguments?.getString(NavRoute.Editor.DATE_KEY)
                ?: LocalDate.now().toString()
            val date = LocalDate.parse(dateStr)
            val plannerMode by appSettings.plannerMode.collectAsState(
                initial = com.wisecal.android.model.PlannerMode.TODO
            )
            val vm: EventEditorViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(c: Class<T>): T =
                        EventEditorViewModel(
                            calendarRepo = calendarRepo,
                            onSaved = { navController.popBackStack() },
                        ) as T
                }
            )
            LaunchedEffect(date, plannerMode) { vm.initNew(plannerMode, date) }
            EventEditorScreen(
                viewModel         = vm,
                templates         = templates,
                defaultCalendarId = 0L,
                onDismiss         = { navController.popBackStack() },
            )
        }
    }

    // ── 이벤트 복사 오버레이 ─────────────────────────────────────────
    copyTarget?.let { event ->
        val copyVm: EventCopyViewModel = viewModel(
            key     = "copy-${event.id}",
            factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(c: Class<T>): T =
                    EventCopyViewModel(calendarRepo) as T
            }
        )
        LaunchedEffect(event) { copyVm.init(event, copyDate) }
        EventCopySheet(
            viewModel = copyVm,
            onDismiss = { copyTarget = null },
        )
    }
}

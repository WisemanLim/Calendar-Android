package com.wisecal.android.ui.onboarding

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wisecal.android.model.PlannerMode

// ── 색상 ────────────────────────────────────────────────────────────────────
private val IosBlue       = Color(0xFF007AFF)
private val IosLightGray  = Color(0xFFF2F2F7)
private val IosGroupBg    = Color(0xFFFFFFFF)
private val IosDarkText   = Color(0xFF1C1C1E)
private val IosGrayText   = Color(0xFF8E8E93)
private val IosCardBlue   = Color(0xFFEAF3FF)
private val IosCardYellow = Color(0xFFFFF8E7)
private val IosCardGreen  = Color(0xFFECFFF3)
private val IosCardPink   = Color(0xFFFFEEF2)

/**
 * 온보딩 화면 — iOS WiseCal 온보딩 대응.
 * WELCOME(+모드선택) → INPUT_MODE → PERSONAL_EVENT → INTEGRATION → COMPLETE
 */
@Composable
fun OnboardingScreen(
    state: OnboardingState,
    onStateChange: (OnboardingState) -> Unit,
    onComplete: (PlannerMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    val calendarLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results[Manifest.permission.READ_CALENDAR] == true &&
                results[Manifest.permission.WRITE_CALENDAR] == true
        onStateChange(state.copy(calendarPermissionGranted = granted))
    }
    val notificationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        onStateChange(state.copy(notificationPermissionGranted = granted))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(IosLightGray)
            .systemBarsPadding(),
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (state.currentStep) {
                OnboardingStep.WELCOME ->
                    WelcomePage(
                        selectedMode = state.selectedPlannerMode,
                        onSelectMode = { onStateChange(state.copy(selectedPlannerMode = it)) },
                    )
                OnboardingStep.INPUT_MODE     -> InputModePage()
                OnboardingStep.PERSONAL_EVENT -> PersonalEventPage()
                OnboardingStep.INTEGRATION    ->
                    IntegrationPage(
                        calendarGranted = state.calendarPermissionGranted,
                        notificationGranted = state.notificationPermissionGranted,
                        onRequestCalendar = {
                            calendarLauncher.launch(arrayOf(
                                Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_CALENDAR,
                            ))
                        },
                        onRequestNotification = {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onStateChange(state.copy(notificationPermissionGranted = true))
                            }
                        },
                    )
                OnboardingStep.COMPLETE -> {}
            }
        }

        // 하단 버튼
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(IosLightGray)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                onClick = {
                    if (state.isLastStep) onComplete(state.selectedPlannerMode ?: PlannerMode.TODO)
                    else onStateChange(state.nextStep())
                },
                enabled = state.canProceed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IosBlue,
                    contentColor = Color.White,
                    disabledContainerColor = IosBlue.copy(alpha = 0.4f),
                ),
                shape = RoundedCornerShape(14.dp),
            ) {
                Text(
                    text = if (state.isLastStep) "시작하기" else "다음",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ── Page 1: Welcome + 플래너 모드 선택 ──────────────────────────────────────

@Composable
private fun WelcomePage(
    selectedMode: PlannerMode?,
    onSelectMode: (PlannerMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "📅", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Wise Calendar",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = IosDarkText,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "나만의 Android 플래너 앱", fontSize = 16.sp, color = IosGrayText)
        Spacer(modifier = Modifier.height(24.dp))

        // 소개 카드
        IosCard(bgColor = IosCardBlue) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Text("😊", fontSize = 32.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "오늘 일정을 한눈에!\n크게 보고 빠르게 입력하자 📅",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = IosDarkText,
                        lineHeight = 22.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "좌우 스와이프로 날짜 이동 · 하단 달력으로 빠른 점프",
                        fontSize = 12.sp,
                        color = IosGrayText,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        FeatureRow(emoji = "🗓️", text = "큰 날짜 히어로 화면")
        FeatureRow(emoji = "↔️", text = "스와이프로 날짜·월·연도 이동")
        FeatureRow(emoji = "⛅", text = "주간 날씨 + 이벤트 현황 한눈에")
        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "사용 방식 선택",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = IosDarkText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
        )
        ModeCard(mode = PlannerMode.DIARY, emoji = "📖",
            selected = selectedMode == PlannerMode.DIARY, onClick = { onSelectMode(PlannerMode.DIARY) })
        Spacer(modifier = Modifier.height(8.dp))
        ModeCard(mode = PlannerMode.TODO, emoji = "🗒️",
            selected = selectedMode == PlannerMode.TODO, onClick = { onSelectMode(PlannerMode.TODO) })
        Spacer(modifier = Modifier.height(8.dp))
        ModeCard(mode = PlannerMode.PERSONAL_EVENT, emoji = "品",
            selected = selectedMode == PlannerMode.PERSONAL_EVENT, onClick = { onSelectMode(PlannerMode.PERSONAL_EVENT) })

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("⚙️", fontSize = 14.sp)
            Text(
                text = "선택한 방식은 설정에서 언제든지 변경할 수 있습니다.",
                fontSize = 13.sp,
                color = IosGrayText,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeatureRow(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 18.sp, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
        Text(text, fontSize = 15.sp, color = IosDarkText)
    }
}

@Composable
private fun ModeCard(mode: PlannerMode, emoji: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) IosBlue else Color(0xFFD1D1D6)
    val bgColor     = if (selected) IosBlue.copy(alpha = 0.06f) else IosGroupBg
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (selected) 1.5.dp else 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(emoji, fontSize = 22.sp, modifier = Modifier.width(28.dp), textAlign = TextAlign.Center)
        Text(
            text = mode.title,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = IosDarkText,
            modifier = Modifier.weight(1f),
        )
        if (selected) Text("✓", fontSize = 18.sp, color = IosBlue, fontWeight = FontWeight.Bold)
    }
}

// ── Page 2: 입력 모드 소개 ────────────────────────────────────────────────

@Composable
private fun InputModePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("📖", fontSize = 48.sp)
            Text("🗒️", fontSize = 48.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("입력 모드", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
        Spacer(modifier = Modifier.height(6.dp))
        Text("두 가지 기록 방식을 골라 사용하세요", fontSize = 15.sp, color = IosGrayText)
        Spacer(modifier = Modifier.height(28.dp))

        ModeIntroHeader(emoji = "📖", title = "일기 모드", subtitle = "하루를 글로 기록합니다")
        Spacer(modifier = Modifier.height(10.dp))
        IosCard(bgColor = IosCardYellow) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text("👨‍💻", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "오늘 드디어 프로젝트 완료...\n소회를 남겨야겠다 ✏️",
                        fontSize = 14.sp, color = IosDarkText, lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("시작 → 저장 시각 자동 기록", fontSize = 11.sp, color = IosGrayText)
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        FeatureRow(emoji = "✍️", text = "자유로운 본문 입력")
        FeatureRow(emoji = "⏱️", text = "에디터 열린 시각 = 시작, 저장 시각 = 종료 자동")
        FeatureRow(emoji = "🕐", text = "경과 시간 실시간 표시")

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

        ModeIntroHeader(emoji = "🗒️", title = "일정관리 모드", subtitle = "잊지 말아야 할 일들을 체크리스트로")
        Spacer(modifier = Modifier.height(10.dp))
        IosCard(bgColor = IosCardBlue) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text("😱", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "또 잊어버렸다...\n이번엔 체크리스트로 관리해야지!",
                        fontSize = 14.sp, color = IosDarkText, lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("✅ 기획서 제출" to true, "☐ 팀 미팅 자료 준비" to false, "☐ 클라이언트 회신" to false)
                        .forEach { (text, done) ->
                            Text(
                                text = text,
                                fontSize = 13.sp,
                                color = if (done) IosGrayText else IosDarkText,
                            )
                        }
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        FeatureRow(emoji = "☑️", text = "항목별 완료 토글")
        FeatureRow(emoji = "📅", text = "기한(Due Date) 필수 설정")
        FeatureRow(emoji = "🔴", text = "기한 초과 시 빨간색 표시")
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ModeIntroHeader(emoji: String, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(emoji, fontSize = 28.sp)
        Column {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
            Text(subtitle, fontSize = 13.sp, color = IosGrayText)
        }
    }
}

// ── Page 3: 개인 이벤트 ───────────────────────────────────────────────────

@Composable
private fun PersonalEventPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Text("📦", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text("개인 이벤트", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
        Spacer(modifier = Modifier.height(6.dp))
        Text("템플릿으로 빠르게 입력하세요", fontSize = 15.sp, color = IosGrayText)
        Spacer(modifier = Modifier.height(28.dp))

        IosCard(bgColor = IosCardGreen) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🧑‍💊", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("복용", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
                        Text(
                            "📷 사진 첨부 지원", fontSize = 11.sp, color = IosBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IosBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                    Text("아침 7시 · 점심 12시 · 저녁 18시", fontSize = 12.sp, color = IosGrayText)
                    Text(
                        "아침/점심/저녁 프리셋으로 빠르게 입력하고\n약 봉투 사진도 카메라·앨범으로 첨부 가능",
                        fontSize = 13.sp, color = IosDarkText, lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        IosCard(bgColor = IosCardPink) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🥰", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text("약속", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
                    Text("연인 · 친구 · 회식", fontSize = 12.sp, color = IosGrayText)
                    Text(
                        "기다려지는 만남을 등록하세요.\n장소(location)를 입력하면 지도 앱과 연동됩니다.",
                        fontSize = 13.sp, color = IosDarkText, lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        IosCard(bgColor = IosCardBlue) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("🔄", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("고정", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
                        Text(
                            "↺ 매년 자동 반복", fontSize = 11.sp, color = IosBlue,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(IosBlue.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        )
                    }
                    Text("정기 작업 · 연간 반복", fontSize = 12.sp, color = IosGrayText)
                    Text(
                        "매년 반복되는 일정을 등록합니다.\n연중 일련번호로 자동 태그됩니다.",
                        fontSize = 13.sp, color = IosDarkText, lineHeight = 19.sp,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Page 4: 연동 & 알림 ────────────────────────────────────────────────────

@Composable
private fun IntegrationPage(
    calendarGranted: Boolean,
    notificationGranted: Boolean,
    onRequestCalendar: () -> Unit,
    onRequestNotification: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("🔗", fontSize = 48.sp)
            Text("🔔", fontSize = 48.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("연동 & 알림", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = IosDarkText)
        Spacer(modifier = Modifier.height(6.dp))
        Text("캘린더 연동과 알림을 설정하세요", fontSize = 15.sp, color = IosGrayText)
        Spacer(modifier = Modifier.height(28.dp))

        IosCard(bgColor = IosCardGreen) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text("📱", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Google 캘린더 일정이\n자동으로 여기 보이네! 🎉",
                        fontSize = 14.sp, color = IosDarkText, lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "설정 → 캘린더 → 계정 추가 → Google/iCloud 로그인",
                        fontSize = 11.sp, color = IosGrayText,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (!calendarGranted) {
            Button(
                onClick = onRequestCalendar,
                modifier = Modifier.fillMaxWidth().height(46.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IosBlue),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("캘린더 접근 허용", fontSize = 15.sp)
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE8F8EC))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("✅", fontSize = 16.sp)
                Text("캘린더 접근이 허용되었습니다.", fontSize = 14.sp, color = Color(0xFF1C7A3B))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FeatureRow(emoji = "📋", text = "Google·기기 캘린더 자동 표시")
        FeatureRow(emoji = "☑️", text = "앱 설정에서 표시 캘린더 선택")
        FeatureRow(emoji = "🔒", text = "기기 계정 기반 — 별도 로그인 불필요")

        HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), thickness = 0.5.dp)

        IosCard(bgColor = Color(0xFFFFF3E8)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Text("🔔", fontSize = 28.sp)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "오전 9시 알림 덕분에\n일정을 하나도 안 놓쳤다!",
                        fontSize = 14.sp, color = IosDarkText, lineHeight = 20.sp,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (!notificationGranted) {
                        OutlinedButton(
                            onClick = onRequestNotification,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF9500)),
                            shape = RoundedCornerShape(10.dp),
                        ) {
                            Text("알림 허용", fontWeight = FontWeight.Medium)
                        }
                    } else {
                        Text("✅ 알림이 허용되었습니다.", fontSize = 13.sp, color = Color(0xFF34C759))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        FeatureRow(emoji = "⏰", text = "이벤트 저장 시 당일 오전 9시 알림 예약")
        FeatureRow(emoji = "⚙️", text = "설정에서 언제든지 알림 on/off 변경 가능")
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── 공통 ─────────────────────────────────────────────────────────────────

@Composable
private fun IosCard(
    bgColor: Color = IosGroupBg,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .padding(16.dp),
        content = content,
    )
}

package com.wisecal.android.ui.onboarding

import com.wisecal.android.model.PlannerMode

enum class OnboardingStep {
    WELCOME,         // 앱 소개 + 플래너 모드 선택
    INPUT_MODE,      // 일기 + 일정관리 모드 소개
    PERSONAL_EVENT,  // 개인 이벤트 모드 소개
    INTEGRATION,     // 캘린더 연동 + 알림 설정
    COMPLETE,
}

/**
 * 온보딩 흐름 상태.
 * WELCOME → INPUT_MODE → PERSONAL_EVENT → INTEGRATION → COMPLETE
 */
data class OnboardingState(
    val currentStep: OnboardingStep = OnboardingStep.WELCOME,
    val calendarPermissionGranted: Boolean = false,
    val notificationPermissionGranted: Boolean = false,
    val selectedPlannerMode: PlannerMode? = PlannerMode.PERSONAL_EVENT,
) {
    val canProceed: Boolean
        get() = when (currentStep) {
            OnboardingStep.WELCOME -> selectedPlannerMode != null
            else -> true
        }

    val isLastStep: Boolean
        get() = currentStep == OnboardingStep.INTEGRATION

    val isComplete: Boolean
        get() = currentStep == OnboardingStep.COMPLETE

    fun nextStep(): OnboardingState {
        val next = when (currentStep) {
            OnboardingStep.WELCOME        -> OnboardingStep.INPUT_MODE
            OnboardingStep.INPUT_MODE     -> OnboardingStep.PERSONAL_EVENT
            OnboardingStep.PERSONAL_EVENT -> OnboardingStep.INTEGRATION
            OnboardingStep.INTEGRATION    -> OnboardingStep.COMPLETE
            OnboardingStep.COMPLETE       -> OnboardingStep.COMPLETE
        }
        return copy(currentStep = next)
    }
}

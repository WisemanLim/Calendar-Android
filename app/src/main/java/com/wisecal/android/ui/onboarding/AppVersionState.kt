package com.wisecal.android.ui.onboarding

/**
 * 온보딩 노출 조건 판정.
 * PRD §3.9: 최초설치(onboardingCompleted=false) · 버전 변경 · DEBUG 매회.
 */
data class AppVersionState(
    val currentVersionName: String,
    val lastSeenVersion: String,
    val onboardingCompleted: Boolean,
    val isDebugBuild: Boolean,
) {
    /** true → 온보딩 표시 */
    val shouldShowOnboarding: Boolean
        get() = isDebugBuild || !onboardingCompleted || lastSeenVersion != currentVersionName

    /** 온보딩 완료 후 저장할 버전 */
    val versionToSave: String get() = currentVersionName
}

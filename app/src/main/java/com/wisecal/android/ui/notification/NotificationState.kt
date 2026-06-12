package com.wisecal.android.ui.notification

/**
 * 알림 권한 + 활성화 상태.
 * Android 13+(API 33) POST_NOTIFICATIONS 런타임 권한 필요.
 * PRD §3.3 알림 설정 대응.
 */
data class NotificationState(
    val notificationsEnabled: Boolean = false,
    val hasPermission: Boolean = false,
    val requiresRuntimePermission: Boolean = false,
) {
    /** 활성화 ON + 권한 없음 + Android 13+ → 권한 요청 필요 */
    val shouldRequestPermission: Boolean
        get() = notificationsEnabled && !hasPermission && requiresRuntimePermission

    /** 실제로 알림 발송 가능 상태 */
    val isActive: Boolean
        get() = notificationsEnabled && (hasPermission || !requiresRuntimePermission)

    val canSchedule: Boolean get() = isActive
}

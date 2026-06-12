package com.wisecal.android.ui.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NotificationStateTest {

    // ── 기본값 ─────────────────────────────────────────────────

    @Test
    fun `default state notifications disabled`() {
        assertFalse(NotificationState().notificationsEnabled)
    }

    @Test
    fun `default state hasPermission false`() {
        assertFalse(NotificationState().hasPermission)
    }

    @Test
    fun `default state isActive false`() {
        assertFalse(NotificationState().isActive)
    }

    // ── shouldRequestPermission ────────────────────────────────

    @Test
    fun `shouldRequestPermission false when notifications disabled`() {
        val s = NotificationState(notificationsEnabled = false, hasPermission = false, requiresRuntimePermission = true)
        assertFalse(s.shouldRequestPermission)
    }

    @Test
    fun `shouldRequestPermission false when has permission`() {
        val s = NotificationState(notificationsEnabled = true, hasPermission = true, requiresRuntimePermission = true)
        assertFalse(s.shouldRequestPermission)
    }

    @Test
    fun `shouldRequestPermission true when enabled no permission and Android13+`() {
        val s = NotificationState(notificationsEnabled = true, hasPermission = false, requiresRuntimePermission = true)
        assertTrue(s.shouldRequestPermission)
    }

    @Test
    fun `shouldRequestPermission false when enabled no permission but pre-Android13`() {
        val s = NotificationState(notificationsEnabled = true, hasPermission = false, requiresRuntimePermission = false)
        assertFalse(s.shouldRequestPermission)
    }

    // ── isActive ───────────────────────────────────────────────

    @Test
    fun `isActive false when notifications disabled even with permission`() {
        val s = NotificationState(notificationsEnabled = false, hasPermission = true)
        assertFalse(s.isActive)
    }

    @Test
    fun `isActive true when enabled and has permission`() {
        val s = NotificationState(notificationsEnabled = true, hasPermission = true, requiresRuntimePermission = true)
        assertTrue(s.isActive)
    }

    @Test
    fun `isActive true when enabled and pre-Android13 no runtime permission needed`() {
        val s = NotificationState(notificationsEnabled = true, hasPermission = false, requiresRuntimePermission = false)
        assertTrue(s.isActive)
    }

    // ── canSchedule ────────────────────────────────────────────

    @Test
    fun `canSchedule equals isActive`() {
        val active = NotificationState(notificationsEnabled = true, hasPermission = true)
        assertTrue(active.canSchedule)
        val inactive = NotificationState(notificationsEnabled = false)
        assertFalse(inactive.canSchedule)
    }
}

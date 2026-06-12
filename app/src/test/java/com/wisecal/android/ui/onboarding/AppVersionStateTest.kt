package com.wisecal.android.ui.onboarding

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionStateTest {

    private fun state(
        currentVersion: String = "1.0",
        lastSeen: String = "1.0",
        completed: Boolean = true,
        debug: Boolean = false,
    ) = AppVersionState(currentVersion, lastSeen, completed, debug)

    // ── shouldShowOnboarding: false 조건 ───────────────────────

    @Test
    fun `shouldShowOnboarding false when completed version-match non-debug`() {
        assertFalse(state().shouldShowOnboarding)
    }

    // ── shouldShowOnboarding: true 조건 ───────────────────────

    @Test
    fun `shouldShowOnboarding true when debug build`() {
        assertTrue(state(debug = true).shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding true when onboarding not completed`() {
        assertTrue(state(completed = false).shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding true when version mismatch`() {
        assertTrue(state(currentVersion = "1.1", lastSeen = "1.0").shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding true when all three conditions true`() {
        assertTrue(state(currentVersion = "2.0", lastSeen = "1.0", completed = false, debug = true).shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding true when completed but version mismatch`() {
        assertTrue(state(currentVersion = "2.0", lastSeen = "1.0", completed = true).shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding false when debug false completed true version match`() {
        assertFalse(state(currentVersion = "3.5", lastSeen = "3.5", completed = true, debug = false).shouldShowOnboarding)
    }

    // ── versionToSave ──────────────────────────────────────────

    @Test
    fun `versionToSave equals currentVersionName`() {
        assertEquals("2.1.0", state(currentVersion = "2.1.0").versionToSave)
    }

    @Test
    fun `versionToSave is not lastSeenVersion`() {
        val s = state(currentVersion = "2.0", lastSeen = "1.9")
        assertEquals("2.0", s.versionToSave)
    }

    // ── 엣지 케이스 ────────────────────────────────────────────

    @Test
    fun `shouldShowOnboarding true when lastSeen empty string`() {
        assertTrue(state(currentVersion = "1.0", lastSeen = "").shouldShowOnboarding)
    }

    @Test
    fun `shouldShowOnboarding false when both versions empty and completed`() {
        assertFalse(state(currentVersion = "", lastSeen = "", completed = true, debug = false).shouldShowOnboarding)
    }

    @Test
    fun `debug true overrides completed and version match`() {
        // Even if completed and version match, debug=true forces show
        assertTrue(state(currentVersion = "1.0", lastSeen = "1.0", completed = true, debug = true).shouldShowOnboarding)
    }
}

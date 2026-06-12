package com.wisecal.android.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EventImageStoreTest {

    // ── fileName 포맷 ──────────────────────────────────────────

    @Test
    fun `fileName starts with event_ prefix`() {
        assertTrue(EventImageStore.fileName(1L, 1000L).startsWith("event_"))
    }

    @Test
    fun `fileName ends with jpg extension`() {
        assertTrue(EventImageStore.fileName(1L, 1000L).endsWith(".jpg"))
    }

    @Test
    fun `fileName contains eventId`() {
        val name = EventImageStore.fileName(42L, 1000L)
        assertTrue(name.contains("42"))
    }

    @Test
    fun `fileName contains timestamp`() {
        val name = EventImageStore.fileName(1L, 9999999L)
        assertTrue(name.contains("9999999"))
    }

    @Test
    fun `fileName full format`() {
        assertEquals("event_7_1234567890.jpg", EventImageStore.fileName(7L, 1234567890L))
    }

    @Test
    fun `different eventIds produce different names`() {
        val ts = 5000L
        assertNotEquals(EventImageStore.fileName(1L, ts), EventImageStore.fileName(2L, ts))
    }

    // ── extractEventId ─────────────────────────────────────────

    @Test
    fun `extractEventId from valid fileName`() {
        val name = EventImageStore.fileName(42L, 1000L)
        assertEquals(42L, EventImageStore.extractEventId(name))
    }

    @Test
    fun `extractEventId wrong prefix returns null`() {
        assertNull(EventImageStore.extractEventId("photo_42_1000.jpg"))
    }

    @Test
    fun `extractEventId wrong extension returns null`() {
        assertNull(EventImageStore.extractEventId("event_42_1000.png"))
    }

    @Test
    fun `extractEventId empty string returns null`() {
        assertNull(EventImageStore.extractEventId(""))
    }

    // ── isEventImage ───────────────────────────────────────────

    @Test
    fun `isEventImage true for valid fileName`() {
        assertTrue(EventImageStore.isEventImage("event_1_1000.jpg"))
    }

    @Test
    fun `isEventImage false for wrong prefix`() {
        assertFalse(EventImageStore.isEventImage("img_1_1000.jpg"))
    }

    @Test
    fun `isEventImage false for wrong extension`() {
        assertFalse(EventImageStore.isEventImage("event_1_1000.png"))
    }
}

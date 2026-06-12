package com.wisecal.android.ui

import com.wisecal.android.model.WeatherInfo
import org.junit.Assert.*
import org.junit.Test

class WeatherInfoTest {

    private fun w(code: Int, temp: Double = 20.0) = WeatherInfo(temp, code)

    // ── iconEmoji ────────────────────────────────────────────────

    @Test fun `code 0 clear is sun`()          { assertEquals("☀️", w(0).iconEmoji) }
    @Test fun `code 1 mostly clear`()          { assertEquals("🌤️", w(1).iconEmoji) }
    @Test fun `code 2 partly cloudy`()         { assertEquals("🌤️", w(2).iconEmoji) }
    @Test fun `code 3 overcast is cloud`()     { assertEquals("☁️", w(3).iconEmoji) }
    @Test fun `code 45 fog`()                  { assertEquals("🌫️", w(45).iconEmoji) }
    @Test fun `code 51 drizzle`()              { assertEquals("🌦️", w(51).iconEmoji) }
    @Test fun `code 61 rain`()                 { assertEquals("🌧️", w(61).iconEmoji) }
    @Test fun `code 71 snow`()                 { assertEquals("❄️", w(71).iconEmoji) }
    @Test fun `code 95 thunderstorm`()         { assertEquals("⛈️", w(95).iconEmoji) }
    @Test fun `unknown code fallback thermometer`() { assertEquals("🌡️", w(999).iconEmoji) }

    // ── displayText ──────────────────────────────────────────────

    @Test fun `displayText truncates to int degrees`() {
        assertEquals("22°", w(0, 22.9).displayText)
    }

    @Test fun `displayText negative temperature`() {
        assertEquals("-5°", w(0, -5.9).displayText)
    }

    // ── description ──────────────────────────────────────────────

    @Test fun `description for clear is 맑음`() {
        assertEquals("맑음", w(0).description)
    }
}

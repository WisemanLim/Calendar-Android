package com.wisecal.android.model

/**
 * Open-Meteo 현재 날씨 정보.
 * WMO Weather Interpretation Code 기반.
 * iOS WeatherKit WKWeatherCondition 대응.
 */
data class WeatherInfo(
    val temperatureCelsius: Double,
    val weatherCode: Int,
) {
    val iconEmoji: String
        get() = when (weatherCode) {
            0            -> "☀️"
            1, 2         -> "🌤️"
            3            -> "☁️"
            45, 48       -> "🌫️"
            in 51..55    -> "🌦️"
            in 61..65    -> "🌧️"
            in 71..77    -> "❄️"
            in 80..82    -> "🌧️"
            95, 96, 99   -> "⛈️"
            else         -> "🌡️"
        }

    val description: String
        get() = when (weatherCode) {
            0            -> "맑음"
            1            -> "대체로 맑음"
            2            -> "구름 조금"
            3            -> "흐림"
            45, 48       -> "안개"
            in 51..55    -> "이슬비"
            in 61..65    -> "비"
            in 71..77    -> "눈"
            in 80..82    -> "소나기"
            95, 96, 99   -> "천둥번개"
            else         -> ""
        }

    /** 목록 표시용: `22°` */
    val displayText: String get() = "${temperatureCelsius.toInt()}°"
}

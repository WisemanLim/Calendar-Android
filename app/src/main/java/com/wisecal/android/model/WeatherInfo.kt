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
            0, 1                   -> "☀️"   // clear
            2                      -> "⛅"   // partlyCloudy
            3                      -> "☁️"  // overcast
            45, 48                 -> "🌫️"  // fog
            51, 53, 55, 56, 57     -> "🌦️"  // drizzle
            61, 63, 65, 66, 67,
            80, 81, 82             -> "🌧️"  // rain / heavyRain
            71, 73, 75, 77,
            85, 86                 -> "❄️"  // snow
            95, 96, 99             -> "⛈️"  // thunderstorm
            else                   -> "☀️"  // clear (iOS default)
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

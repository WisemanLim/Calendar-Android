package com.wisecal.android.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

/** Open-Meteo /v1/forecast — 현재 날씨 조회. */
interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude")  latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current")   current: String = "temperature_2m,weather_code",
        @Query("timezone")  timezone: String = "auto",
    ): WeatherResponse
}

data class WeatherResponse(
    @SerializedName("current") val current: CurrentWeatherDto,
)

data class CurrentWeatherDto(
    @SerializedName("temperature_2m") val temperatureCelsius: Double,
    @SerializedName("weather_code")   val weatherCode: Int,
)

/** Open-Meteo /v1/forecast — 7일 일별 예보. */
interface WeatherForecastService {
    @GET("v1/forecast")
    suspend fun getDailyForecast(
        @Query("latitude")      latitude: Double,
        @Query("longitude")     longitude: Double,
        @Query("daily")         daily: String = "weather_code,temperature_2m_max",
        @Query("timezone")      timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = 7,
    ): DailyForecastResponse
}

data class DailyForecastResponse(
    @SerializedName("daily") val daily: DailyDto,
)

data class DailyDto(
    @SerializedName("time")               val time: List<String>,
    @SerializedName("weather_code")       val weatherCode: List<Int>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
)

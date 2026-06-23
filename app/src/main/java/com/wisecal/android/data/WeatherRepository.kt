package com.wisecal.android.data

import com.wisecal.android.model.WeatherInfo
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): WeatherInfo?
    suspend fun getWeeklyForecast(latitude: Double, longitude: Double): Map<LocalDate, WeatherInfo>
}

class OpenMeteoWeatherRepository(
    private val service: WeatherApiService,
    private val forecastService: WeatherForecastService,
) : WeatherRepository {

    // (data, fetchedAtMs) — 각각 독립 TTL
    private var cachedCurrent: Pair<WeatherInfo, Long>? = null
    private var cachedWeekly:  Pair<Map<LocalDate, WeatherInfo>, Long>? = null
    private val cacheTtlMs = 30 * 60 * 1000L  // 30분

    override suspend fun getWeather(latitude: Double, longitude: Double): WeatherInfo? {
        cachedCurrent?.let { (data, ts) ->
            if (System.currentTimeMillis() - ts < cacheTtlMs) return data
        }
        return runCatching {
            val dto = service.getCurrentWeather(latitude, longitude)
            WeatherInfo(dto.current.temperatureCelsius, dto.current.weatherCode)
        }.getOrNull()?.also { cachedCurrent = it to System.currentTimeMillis() }
    }

    override suspend fun getWeeklyForecast(latitude: Double, longitude: Double): Map<LocalDate, WeatherInfo> {
        cachedWeekly?.let { (data, ts) ->
            if (System.currentTimeMillis() - ts < cacheTtlMs) return data
        }
        return runCatching {
            val dto = forecastService.getDailyForecast(latitude, longitude)
            dto.daily.time.mapIndexedNotNull { i, dateStr ->
                runCatching {
                    val date = LocalDate.parse(dateStr)
                    val code = dto.daily.weatherCode.getOrElse(i) { 0 }
                    val temp = dto.daily.tempMax.getOrElse(i) { 0.0 }
                    date to WeatherInfo(temp, code)
                }.getOrNull()
            }.toMap()
        }.getOrElse { emptyMap() }.also { map ->
            if (map.isNotEmpty()) cachedWeekly = map to System.currentTimeMillis()
        }
    }

    companion object {
        private const val BASE_URL = "https://api.open-meteo.com/"

        fun create(debug: Boolean = false): OpenMeteoWeatherRepository {
            val client = OkHttpClient.Builder().apply {
                if (debug) addInterceptor(
                    HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
                )
            }.build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return OpenMeteoWeatherRepository(
                service = retrofit.create(WeatherApiService::class.java),
                forecastService = retrofit.create(WeatherForecastService::class.java),
            )
        }
    }
}

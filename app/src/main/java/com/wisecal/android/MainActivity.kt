package com.wisecal.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.wisecal.android.data.AndroidCalendarRepository
import com.wisecal.android.data.AppSettings
import com.wisecal.android.data.OpenMeteoWeatherRepository
import com.wisecal.android.data.TemplateStore
import com.wisecal.android.data.settingsDataStore
import com.wisecal.android.data.templatesDataStore
import com.wisecal.android.ui.navigation.WiseCalNavGraph
import com.wisecal.android.ui.theme.WiseCalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appSettings   = AppSettings(settingsDataStore)
        val calendarRepo  = AndroidCalendarRepository(this)
        val weatherRepo   = OpenMeteoWeatherRepository.create(debug = BuildConfig.DEBUG)
        val templateStore = TemplateStore(templatesDataStore)

        setContent {
            WiseCalTheme(appSettings = appSettings) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WiseCalNavGraph(
                        appSettings   = appSettings,
                        calendarRepo  = calendarRepo,
                        weatherRepo   = weatherRepo,
                        templateStore = templateStore,
                    )
                }
            }
        }
    }
}

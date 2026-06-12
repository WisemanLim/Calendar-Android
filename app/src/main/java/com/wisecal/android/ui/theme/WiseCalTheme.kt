package com.wisecal.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import com.wisecal.android.data.AppSettings
import com.wisecal.android.model.AppColorScheme

/**
 * AppColorScheme DataStore 값을 읽어 DarkTheme/LightTheme 적용.
 * iOS AppCompatDelegate.setDefaultNightMode() 대응.
 */
@Composable
fun WiseCalTheme(
    appSettings: AppSettings,
    content: @Composable () -> Unit,
) {
    val colorScheme by appSettings.appColorScheme.collectAsState(initial = AppColorScheme.SYSTEM)
    val systemDark = isSystemInDarkTheme()
    val useDark = when (colorScheme) {
        AppColorScheme.DARK   -> true
        AppColorScheme.LIGHT  -> false
        AppColorScheme.SYSTEM -> systemDark
    }
    MaterialTheme(
        colorScheme = if (useDark) darkColorScheme() else lightColorScheme(),
        content     = content,
    )
}

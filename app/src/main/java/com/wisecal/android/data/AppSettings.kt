package com.wisecal.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.wisecal.android.model.AppColorScheme
import com.wisecal.android.model.PlannerMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class AppSettings(private val dataStore: DataStore<Preferences>) {

    companion object Keys {
        val PLANNER_MODE          = stringPreferencesKey("plannerMode")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notificationsEnabled")
        val ONBOARDING_COMPLETED  = booleanPreferencesKey("onboardingCompleted")
        val ENABLED_CALENDAR_IDS  = stringPreferencesKey("enabledCalendarIDs")
        val LAST_SEEN_VERSION     = stringPreferencesKey("lastSeenVersion")
        val APP_COLOR_SCHEME      = stringPreferencesKey("appColorScheme")
    }

    val plannerMode: Flow<PlannerMode> = dataStore.data.map {
        PlannerMode.fromString(it[PLANNER_MODE] ?: PlannerMode.TODO.name)
    }

    val notificationsEnabled: Flow<Boolean> = dataStore.data.map {
        it[NOTIFICATIONS_ENABLED] ?: false
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }

    val enabledCalendarIds: Flow<Set<String>> = dataStore.data.map {
        it[ENABLED_CALENDAR_IDS]
            ?.split(",")
            ?.filter(String::isNotBlank)
            ?.toSet()
            ?: emptySet()
    }

    val lastSeenVersion: Flow<String> = dataStore.data.map {
        it[LAST_SEEN_VERSION] ?: ""
    }

    val appColorScheme: Flow<AppColorScheme> = dataStore.data.map {
        AppColorScheme.fromString(it[APP_COLOR_SCHEME] ?: AppColorScheme.SYSTEM.name)
    }

    suspend fun setPlannerMode(mode: PlannerMode) =
        dataStore.edit { it[PLANNER_MODE] = mode.name }

    suspend fun setNotificationsEnabled(enabled: Boolean) =
        dataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }

    suspend fun setOnboardingCompleted(completed: Boolean) =
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }

    suspend fun setEnabledCalendarIds(ids: Set<String>) =
        dataStore.edit { it[ENABLED_CALENDAR_IDS] = ids.joinToString(",") }

    suspend fun setLastSeenVersion(version: String) =
        dataStore.edit { it[LAST_SEEN_VERSION] = version }

    suspend fun setAppColorScheme(scheme: AppColorScheme) =
        dataStore.edit { it[APP_COLOR_SCHEME] = scheme.name }
}

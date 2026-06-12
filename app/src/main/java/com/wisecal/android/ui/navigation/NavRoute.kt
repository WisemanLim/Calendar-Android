package com.wisecal.android.ui.navigation

import java.time.LocalDate

sealed class NavRoute(val route: String) {
    object Main        : NavRoute("main")
    object Onboarding  : NavRoute("onboarding")
    object Search      : NavRoute("search")
    object Settings    : NavRoute("settings")
    object Editor      : NavRoute("editor/{dateKey}") {
        const val DATE_KEY = "dateKey"
        fun build(date: LocalDate): String = "editor/$date"
    }
}

package com.kondenko.pocketwaka.analytics

import androidx.annotation.Size

sealed class Screen(@Size(min = 1L, max = 36L) val name: String) {

    object Auth : Screen("Auth")

    object Summary : Screen("Summary")

    data class Stats(val range: String?) : Screen("Stats($range)")

    object Menu : Screen("Menu")

}
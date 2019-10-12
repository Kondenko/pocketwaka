package com.kondenko.pocketwaka.domain.stats.model

import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.StatusMarker

sealed class StatsUiModel {
    data class Status(override val status: ScreenStatus) : StatsUiModel(), StatusMarker
    data class Info(val humanReadableDailyAverage: String?, val humanReadableTotal: String?) : StatsUiModel()
    data class Stats(val cardTitle: String, val items: List<StatsItem>) : StatsUiModel()
    data class BestDay(val date: String, val time: String, val percentAboveAverage: Int) : StatsUiModel()
}
package com.kondenko.pocketwaka.domain.ranges.model

sealed class StatsUiModel {
    sealed class Status(val lastUpdated: Long? = null) : StatsUiModel() {
        class Loading(lastUpdated: Long? = null): Status(lastUpdated)
        class Offline(lastUpdated: Long? = null): Status(lastUpdated)
    }
    data class Info(val humanReadableDailyAverage: String?, val humanReadableTotal: String?) : StatsUiModel()
    data class Stats(val cardTitle: String, val items: List<StatsItem>) : StatsUiModel()
    data class BestDay(val date: String, val time: String, val percentAboveAverage: Int) : StatsUiModel()
}
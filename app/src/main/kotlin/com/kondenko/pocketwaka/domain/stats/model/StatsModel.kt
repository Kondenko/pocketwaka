package com.kondenko.pocketwaka.domain.stats.model

sealed class StatsModel {
    sealed class Status(val lastUpdated: Long? = null) : StatsModel() {
        class Loading(lastUpdated: Long? = null): Status(lastUpdated)
        class Offline(lastUpdated: Long? = null): Status(lastUpdated)
    }
    data class Info(val humanReadableDailyAverage: String?, val humanReadableTotal: String?) : StatsModel()
    data class Stats(val cardTitle: String, val items: List<StatsItem>) : StatsModel()
    data class BestDay(val date: String, val time: String, val percentAboveAverage: Int) : StatsModel()
}

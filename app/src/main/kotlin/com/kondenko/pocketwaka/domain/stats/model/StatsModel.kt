package com.kondenko.pocketwaka.domain.stats.model

sealed class StatsModel {
    data class Metadata(val range: String, val lastUpdated: Long, val isEmpty: Boolean) : StatsModel()
    data class Info(val humanReadableDailyAverage: String?, val humanReadableTotal: String?) : StatsModel()
    data class Stats(val cardTitle: String, val items: List<StatsItem>) : StatsModel()
    data class BestDay(val date: String, val time: String, val percentAboveAverage: Int) : StatsModel()
}

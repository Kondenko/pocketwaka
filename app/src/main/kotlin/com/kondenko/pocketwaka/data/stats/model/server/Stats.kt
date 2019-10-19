package com.kondenko.pocketwaka.data.stats.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity

class Stats {

    @SerializedName("total_seconds")
    var totalSeconds: Double? = null

    @SerializedName("daily_average")
    var dailyAverage: Int? = null

    @SerializedName("best_day")
    var bestDay: BestDay? = null

    var editors: List<StatsEntity>? = null

    var languages: List<StatsEntity>? = null

    @SerializedName("operating_systems")
    var operatingSystems: List<StatsEntity>? = null

    var projects: List<StatsEntity>? = null

}

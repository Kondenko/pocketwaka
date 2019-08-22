package com.kondenko.pocketwaka.data.summary.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity

data class Summary(
        val end: String,
        val start: String,
        @SerializedName("data")
        val summaryData: List<SummaryData>
)

data class SummaryData(
        val range: Range,
        @SerializedName("grand_total")
        val grandTotal: GrandTotal,
        val categories: List<StatsEntity>,
        val dependencies: List<StatsEntity>,
        val editors: List<StatsEntity>,
        val languages: List<StatsEntity>,
        val machines: List<StatsEntity>,
        @SerializedName("operatingSystems")
        val operatingSystems: List<StatsEntity>,
        val projects: List<StatsEntity>
)

data class Range(
        val date: String,
        val end: String,
        val start: String,
        val text: String,
        val timezone: String
)

data class GrandTotal(
        val digital: String,
        val hours: Int,
        val minutes: Int,
        val text: String,
        @SerializedName("total_seconds")
        val totalSeconds: Float
)

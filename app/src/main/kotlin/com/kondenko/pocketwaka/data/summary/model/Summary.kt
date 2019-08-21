package com.kondenko.pocketwaka.data.summary.model

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.StatsEntity

data class Summary(
        @SerializedName("data")
        val summaryData: List<SummaryData>,
        val end: String,
        val start: String
)

data class SummaryData(
        val categories: List<Any>,
        val dependencies: List<Any>,
        val editors: List<Any>,
        @SerializedName("grand_total")
        val grandTotal: GrandTotal,
        val languages: List<StatsEntity>,
        val machines: List<StatsEntity>,
        @SerializedName("operatingSystems")
        val operatingSystems: List<StatsEntity>,
        val projects: List<StatsEntity>,
        val range: Range
)

data class GrandTotal(
        val digital: String,
        val hours: Int,
        val minutes: Int,
        val text: String,
        @SerializedName("total_seconds")
        val totalSeconds: Float
)

data class Range(
        val date: String,
        val end: String,
        val start: String,
        val text: String,
        val timezone: String
)
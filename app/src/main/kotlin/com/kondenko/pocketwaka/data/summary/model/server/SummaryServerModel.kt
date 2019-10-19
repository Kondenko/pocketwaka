package com.kondenko.pocketwaka.data.summary.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity

data class Summary(
        @SerializedName("data")
        val summaryData: List<SummaryData>
)

data class SummaryData(
        val range: Range,
        @SerializedName("grand_total")
        val grandTotal: GrandTotal,
        val projects: List<StatsEntity>,
        val branches: List<StatsEntity>?
)

data class Range(val date: String)

data class GrandTotal(
        val hours: Int,
        val minutes: Int,
        @SerializedName("total_seconds")
        val totalSeconds: Float
)
package com.kondenko.pocketwaka.data.summary.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity

data class Summary(
      @SerializedName("data")
      val summaryData: List<SummaryData>
)

data class SummaryData(
      @SerializedName("grand_total")
      val grandTotal: GrandTotal,
      val projects: List<StatsEntity>
)

data class Range(val date: String)

data class GrandTotal(
      @SerializedName("total_seconds")
      val totalSeconds: Float
)
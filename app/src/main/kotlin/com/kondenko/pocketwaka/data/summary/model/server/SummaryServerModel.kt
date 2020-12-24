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
      val projects: List<StatsEntity>,
      val branches: List<StatsEntity>?,
)

data class GrandTotal(
      @SerializedName("total_seconds")
      val totalSeconds: Float
)

operator fun SummaryData?.plus(other: SummaryData): SummaryData {
    this ?: return other
    return SummaryData(
          grandTotal + other.grandTotal,
          projects.merge(other.projects),
          branches?.let { branches.merge(other.branches ?: emptyList()) } ?: other.branches
    )
}

private operator fun GrandTotal?.plus(other: GrandTotal): GrandTotal {
    this ?: return other
    return GrandTotal(totalSeconds + other.totalSeconds)
}

private fun List<StatsEntity>?.merge(other: List<StatsEntity>): List<StatsEntity> {
    this ?: return other
    return (this + other)
          .groupBy { it.name }
          .map { (_, entities) -> entities.reduce(StatsEntity::plus) }
}

private operator fun StatsEntity?.plus(other: StatsEntity): StatsEntity {
    this ?: return other
    require(this.name == other.name) { "Entities are different" }
    return StatsEntity(name, totalSeconds + other.totalSeconds, null)
}
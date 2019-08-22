package com.kondenko.pocketwaka.data.summary.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity

data class Summary(
        val end: String,
        val start: String,
        @SerializedName("data")
        val summaryData: List<SummaryData>,
        @SerializedName("available_branches")
        val availableBranches: List<String>,
        val branches: List<Any>
)

data class SummaryData(
        val range: Range,
        @SerializedName("grand_total")
        val grandTotal: GrandTotal,
        val categories: List<StatsEntity>,
        val dependencies: List<StatsEntity>,
        val editors: List<StatsEntity>,
        val languages: List<StatsEntity>,
        val machines: List<Machine>,
        val entities: List<Entity>,
        @SerializedName("operatingSystems")
        val operatingSystems: List<StatsEntity>,
        val projects: List<StatsEntity>,
        val branches: List<StatsEntity>?
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

data class Machine(
        val digital: String,
        val hours: Int,
        @SerializedName("machine_name_id")
        val machineNameId: String,
        val minutes: Int,
        val name: String,
        val percent: Int,
        val seconds: Int,
        val text: String,
        @SerializedName("total_seconds")
        val totalSeconds: Double
)

data class Entity(
        val digital: String,
        val hours: Int,
        val minutes: Int,
        val name: String,
        val percent: Int,
        val seconds: Int,
        val text: String,
        @SerializedName("total_seconds")
        val totalSeconds: Double,
        val type: String
)

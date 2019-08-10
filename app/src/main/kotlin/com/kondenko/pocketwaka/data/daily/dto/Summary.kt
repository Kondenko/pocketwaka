package com.kondenko.pocketwaka.data.daily.dto

import com.google.gson.annotations.SerializedName

data class Summary(
        @SerializedName("data")
        val summaryData: List<Data>,
        val end: String,
        val start: String
)

data class Data(
        val categories: List<Any>,
        val dependencies: List<Any>,
        val editors: List<Any>,
        @SerializedName("grand_total")
        val grandTotal: GrandTotal,
        val languages: List<Any>,
        val machines: List<Any>,
        @SerializedName("operatingSystems")
        val operatingSystems: List<Any>,
        val projects: List<Any>,
        val range: Range
)

data class GrandTotal(
        val digital: String,
        val hours: Int,
        val minutes: Int,
        val text: String,
        @SerializedName("total_seconds")
        val totalSeconds: Int
)

data class Range(
        val date: String,
        val end: String,
        val start: String,
        val text: String,
        val timezone: String
)
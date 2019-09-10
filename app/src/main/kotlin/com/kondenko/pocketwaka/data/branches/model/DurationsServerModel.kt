package com.kondenko.pocketwaka.data.branches.model

import com.google.gson.annotations.SerializedName

data class DurationsServerModel(
        val branches: List<String>,
        @SerializedName("data")
        val branchesData: List<Duration>,
        val end: String,
        val start: String,
        val timezone: String
)
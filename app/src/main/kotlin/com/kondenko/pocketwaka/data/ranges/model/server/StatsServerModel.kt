package com.kondenko.pocketwaka.data.ranges.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StatsServerModel(
    @SerializedName("data")
    @Expose
    val stats: Stats? = null
)
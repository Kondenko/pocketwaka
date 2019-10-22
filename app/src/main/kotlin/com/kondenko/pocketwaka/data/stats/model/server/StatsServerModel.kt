package com.kondenko.pocketwaka.data.stats.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StatsServerModel(
    @SerializedName("data")
    @Expose
    val stats: Stats? = null
)
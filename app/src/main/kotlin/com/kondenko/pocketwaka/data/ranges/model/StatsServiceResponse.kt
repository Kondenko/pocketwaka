package com.kondenko.pocketwaka.data.ranges.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class StatsServiceResponse(
    @SerializedName("data")
    @Expose
    val stats: Stats? = null
)
package com.kondenko.pocketwaka.data.stats.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatsServiceResponse {

    @SerializedName("data")
    @Expose
    lateinit var stats: Stats

}

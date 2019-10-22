package com.kondenko.pocketwaka.data.stats.model.server

import com.google.gson.annotations.SerializedName

class BestDay {
    var date: String? = null
    @SerializedName("created_at")
    var createdAt: String? = null
    @SerializedName("total_seconds")
    var totalSeconds: Double? = null
}

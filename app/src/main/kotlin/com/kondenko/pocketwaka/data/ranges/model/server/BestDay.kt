package com.kondenko.pocketwaka.data.ranges.model.server

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class BestDay {

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("modified_at")
    @Expose
    var modifiedAt: Any? = null

    @SerializedName("total_seconds")
    @Expose
    var totalSeconds: Double? = null

}

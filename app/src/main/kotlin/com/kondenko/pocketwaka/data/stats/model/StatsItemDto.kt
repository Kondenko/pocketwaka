package com.kondenko.pocketwaka.data.stats.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * The parent class for Editors, Languages etc.
 * Since all these classes contain the same fields,
 * this class is needed to reduce code duplication
 * while providing classes with different names
 * derived from the JSON object.
 *
 * @see com.kondenko.pocketwaka.ui.CardStats
 *
 * @see com.kondenko.pocketwaka.ui.CardStatsListAdapter
 */
open class StatsItemDto  {

    @SerializedName("digital")
    @Expose
    var digital: String? = null

    @SerializedName("hours")
    @Expose
    var hours: Int? = null

    @SerializedName("minutes")
    @Expose
    var minutes: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("percent")
    @Expose
    var percent: Double? = null

    @SerializedName("text")
    @Expose
    var text: String? = null

    @SerializedName("total_seconds")
    @Expose
    var totalSeconds: Double? = null

}

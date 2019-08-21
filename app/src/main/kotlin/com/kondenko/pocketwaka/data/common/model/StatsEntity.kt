package com.kondenko.pocketwaka.data.common.model

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
open class StatsEntity  {
    var digital: String? = null
    var hours: Int? = null
    var minutes: Int? = null
    var seconds: Int? = null
    var name: String? = null
    var percent: Double? = null
    var text: String? = null
    @SerializedName("total_seconds")
    var totalSeconds: Float? = null
}

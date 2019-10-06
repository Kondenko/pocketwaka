package com.kondenko.pocketwaka.data.common.model.database

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
// TODO Replace vars with vals
open class StatsEntity {
    open var digital: String? = null
    open var hours: Int? = null
    open var minutes: Int? = null
    open var seconds: Int? = null
    open var name: String? = null
    open var percent: Double? = null
    open var text: String? = null
    @SerializedName("total_seconds")
    open var totalSeconds: Float? = null
}

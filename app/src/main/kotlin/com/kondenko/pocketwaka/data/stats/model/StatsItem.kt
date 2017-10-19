package com.kondenko.pocketwaka.data.stats.model

import android.os.Parcel
import android.os.Parcelable

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
open class StatsItem : Parcelable {

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
    var totalSeconds: Int? = null

    var color: Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(digital)
        dest.writeValue(hours)
        dest.writeValue(minutes)
        dest.writeValue(name)
        dest.writeValue(percent)
        dest.writeValue(text)
        dest.writeValue(totalSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR: Parcelable.Creator<StatsItem> = object : Parcelable.Creator<StatsItem> {

            override fun createFromParcel(`in`: Parcel): StatsItem {
                val instance = StatsItem()
                instance.digital = `in`.readValue(String::class.java.classLoader) as String
                instance.hours = `in`.readValue(Int::class.java.classLoader) as Int
                instance.minutes = `in`.readValue(Int::class.java.classLoader) as Int
                instance.name = `in`.readValue(String::class.java.classLoader) as String
                instance.percent = `in`.readValue(Double::class.java.classLoader) as Double
                instance.text = `in`.readValue(String::class.java.classLoader) as String
                instance.totalSeconds = `in`.readValue(Int::class.java.classLoader) as Int
                return instance
            }

            override fun newArray(size: Int): Array<StatsItem?> {
                return arrayOfNulls(size)
            }

        }
    }

}

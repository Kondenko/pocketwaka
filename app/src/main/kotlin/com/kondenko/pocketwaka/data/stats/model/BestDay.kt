package com.kondenko.pocketwaka.data.stats.model

import android.content.Context
import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.R

import java.util.concurrent.TimeUnit

class BestDay : Parcelable {

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
    var totalSeconds: Int? = null

    fun getHumanReadableTime(context: Context): String {
        val pattern = context.getString(R.string.stats_time_format)
        val hours = TimeUnit.SECONDS.toHours(totalSeconds!!.toLong())
        val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds!!.toLong()) - TimeUnit.HOURS.toMinutes(hours)
        return String.format(pattern, hours, minutes)
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(createdAt)
        dest.writeValue(date)
        dest.writeValue(id)
        dest.writeValue(modifiedAt)
        dest.writeValue(totalSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR: Parcelable.Creator<BestDay> = object : Parcelable.Creator<BestDay> {
            override fun createFromParcel(`in`: Parcel): BestDay {
                val instance = BestDay()
                instance.createdAt = `in`.readValue(String::class.java.classLoader) as String
                instance.date = `in`.readValue(String::class.java.classLoader) as String
                instance.id = `in`.readValue(String::class.java.classLoader) as String
                instance.modifiedAt = `in`.readValue(Any::class.java.classLoader)
                instance.totalSeconds = `in`.readValue(Int::class.java.classLoader) as Int
                return instance
            }

            override fun newArray(size: Int): Array<BestDay?> {
                return arrayOfNulls(size)
            }

        }
    }

}

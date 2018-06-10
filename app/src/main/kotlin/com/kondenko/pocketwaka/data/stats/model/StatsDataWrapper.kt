package com.kondenko.pocketwaka.data.stats.model

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StatsDataWrapper : Parcelable {

    @SerializedName("data")
    @Expose
    lateinit var stats: Stats

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(stats)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {

        val CREATOR: Parcelable.Creator<StatsDataWrapper> = object : Parcelable.Creator<StatsDataWrapper> {
            override fun createFromParcel(`in`: Parcel): StatsDataWrapper {
                val instance = StatsDataWrapper()
                instance.stats = `in`.readValue(Stats::class.java.classLoader) as Stats
                return instance
            }

            override fun newArray(size: Int): Array<StatsDataWrapper?> {
                return arrayOfNulls(size)
            }

        }
    }

}

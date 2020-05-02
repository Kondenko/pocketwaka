package com.kondenko.pocketwaka.utils.date

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


sealed class DateRange(open val start: Long, open val end: Long) : Parcelable {

    @Parcelize
    data class SingleDay(val time: Long) : DateRange(time, time), Parcelable

    @Parcelize
    data class Range(override val start: Long, override val end: Long) : DateRange(start, end), Parcelable

    enum class PredefinedRange {
        Today
    }

}
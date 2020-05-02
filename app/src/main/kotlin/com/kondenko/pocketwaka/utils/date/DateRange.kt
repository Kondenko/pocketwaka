package com.kondenko.pocketwaka.utils.date

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*


sealed class DateRange(open val start: Long, open val end: Long) : Parcelable {

    @Parcelize
    data class SingleDay(val date: Long) : DateRange(date, date), Parcelable

    @Parcelize
    data class Range(override val start: Long, override val end: Long) : DateRange(start, end), Parcelable

}
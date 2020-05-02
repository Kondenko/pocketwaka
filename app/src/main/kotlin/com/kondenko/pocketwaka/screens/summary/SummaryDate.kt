package com.kondenko.pocketwaka.screens.summary

import android.os.Parcelable
import com.kondenko.pocketwaka.utils.date.DateRange
import kotlinx.android.parcel.Parcelize
import java.util.*

sealed class SummaryDate {

    @Parcelize
    data class SingleDay(val day: Int, val month: Int, val year: Int) : SummaryDate(), Parcelable

    @Parcelize

    data class Range(val start: SingleDay, val end: SingleDay) : SummaryDate(), Parcelable

}

fun SummaryDate.toDateRange() = when (this) {
    is SummaryDate.Range -> toDateRange()
    is SummaryDate.SingleDay -> DateRange(toLongDate(), toLongDate())
}

fun SummaryDate.SingleDay.toLongDate() = Calendar.getInstance().run {
    set(year, month, day)
    time.time
}

fun SummaryDate.Range.toDateRange() = DateRange(start.toLongDate(), end.toLongDate())

fun Date.toSummaryDate() = Calendar.getInstance().run {
    time = this@toSummaryDate
    SummaryDate.SingleDay(get(Calendar.DAY_OF_MONTH), get(Calendar.MONTH), get(Calendar.YEAR))
}
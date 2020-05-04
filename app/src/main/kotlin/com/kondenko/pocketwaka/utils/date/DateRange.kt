package com.kondenko.pocketwaka.utils.date

import android.os.Parcelable
import com.kizitonwose.calendarview.model.CalendarDay
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate


sealed class DateRange(open val start: LocalDate, open val end: LocalDate) : Parcelable {

    @Parcelize
    data class SingleDay(val date: LocalDate) : DateRange(date, date), Parcelable

    @Parcelize
    data class Range(override val start: LocalDate, override val end: LocalDate) : DateRange(start, end), Parcelable

    enum class PredefinedRange {
        Today,
        Yesterday,
        ThisWeek,
        ThisMonth,
        LastWeek,
        LastMonth,
    }

}
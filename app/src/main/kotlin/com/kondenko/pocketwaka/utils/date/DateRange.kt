package com.kondenko.pocketwaka.utils.date

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.threeten.bp.LocalDate


// TODO Rename to Date
sealed class DateRange(open val start: LocalDate, open val end: LocalDate) : Parcelable {

    @Parcelize
    data class SingleDay(val date: LocalDate) : DateRange(date, date), Parcelable

    @Parcelize
    data class Range(override val start: LocalDate, override val end: LocalDate) : DateRange(start, end), Parcelable

    enum class PredefinedRange(val range: DateRange) {

        Today(SingleDay(LocalDate.now())),

        Yesterday(SingleDay(LocalDate.now().minusDays(1))),

        ThisWeek(Range(
              start = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1),
              end = LocalDate.now()
        )),

        LastWeek(Range(
              start = LocalDate.now().minusWeeks(1),
              end = LocalDate.now()
        )),

        ThisMonth(Range(
              start = LocalDate.now().minusDays(LocalDate.now().dayOfMonth.toLong() - 1),
              end = LocalDate.now()
        )),

        LastMonth(Range(
              start = LocalDate.now().minusMonths(1),
              end = LocalDate.now()
        )),

    }

}

operator fun DateRange.contains(date: LocalDate) =
      if (start == end) date == start
      else date == start || date == end || (date.isAfter(start) && date.isBefore(end))
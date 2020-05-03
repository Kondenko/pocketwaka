package com.kondenko.pocketwaka.data.android

import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.isSameDay
import java.util.*
import java.util.concurrent.TimeUnit

class HumanReadableDateFormatter(
      private val dateFormatter: DateFormatter,
      private val dateProvider: DateProvider,
      private val stringProvider: StringProvider
) {

    fun format(date: DateRange) =
          date.asPredefinedRange()?.let(stringProvider::getHumanReadableDateRange)
                ?: formatDefault(date)

    private fun formatDefault(date: DateRange) = when (date) {
        is DateRange.SingleDay -> date.date.formatDate()
        is DateRange.Range -> stringProvider.getDateRangeWithSeparator(date.start.formatDate(), date.end.formatDate())
    }

    private fun Long.formatDate() =
          dateFormatter.formatDateForDisplay(
                TimeUnit.MILLISECONDS.toSeconds(this).toInt(),
                dateProvider.getYear(Date(this)) != dateProvider.getYear()
          )

    @Suppress("IntroduceWhenSubject")
    private fun DateRange.asPredefinedRange(): DateRange.PredefinedRange? = when {
        (this as? DateRange.SingleDay)?.date?.isSameDay(dateProvider.getToday()) == true -> {
            DateRange.PredefinedRange.Today
        }
        else -> {
            null
        }
    }

}
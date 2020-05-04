package com.kondenko.pocketwaka.data.android

import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.isSameDay
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
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

    private fun LocalDate.formatDate() =
          format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)) // TODO Don't show year if eeuals to the current one
//          dateFormatter.formatDateForDisplay(
//                TimeUnit.MILLISECONDS.toSeconds(this).toInt(),
//                dateProvider.getYear(Date(this)) != dateProvider.getYear()
//          )

    @Suppress("IntroduceWhenSubject")
    private fun DateRange.asPredefinedRange(): DateRange.PredefinedRange? = when {
        (this as? DateRange.SingleDay)?.date == dateProvider.getToday() -> {
            DateRange.PredefinedRange.Today
        }
        else -> {
            null
        }
    }

}
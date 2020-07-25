package com.kondenko.pocketwaka.data.android

import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

class HumanReadableDateFormatter(
      private val dateFormatter: DateFormatter,
      private val dateProvider: DateProvider,
      private val stringProvider: StringProvider
) {

    fun format(date: DateRange): String =
          date.asPredefinedRange()
                ?.let(stringProvider::getHumanReadableDateRange)
                ?: formatDefault(date)

    private fun formatDefault(date: DateRange) = when (date) {
        is DateRange.SingleDay -> {
            date.date.formatDate(date.date.year != dateProvider.year)
        }
        is DateRange.Range -> {
            val isNotCurrentYear = dateProvider.year.let { date.start.year < it || date.end.year < it }
            val forceIncludeYear = isNotCurrentYear || date.start.year != date.end.year
            val start = date.start.formatDate(forceIncludeYear)
            val end = date.end.formatDate(forceIncludeYear)
            stringProvider.getDateRangeWithSeparator(start, end)
        }
    }

    private fun LocalDate.formatDate(forceIncludeYear: Boolean) =
          dateFormatter.formatDateForDisplay(
                seconds = atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                includeYear = forceIncludeYear || dateProvider.year != dateProvider.year
          )

    @Suppress("IntroduceWhenSubject")
    private fun DateRange.asPredefinedRange(): DateRange.PredefinedRange? = when {
        (this as? DateRange.SingleDay)?.date == dateProvider.today -> {
            DateRange.PredefinedRange.Today
        }
        else -> {
            null
        }
    }

}
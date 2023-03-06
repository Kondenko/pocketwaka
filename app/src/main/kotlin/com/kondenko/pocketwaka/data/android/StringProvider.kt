package com.kondenko.pocketwaka.data.android

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRange.PredefinedRange.Today

class StringProvider(private val context: Context) {

    fun getSupportEmailSubject(): String =
          context.getString(R.string.menu_support_email_subject)

    fun getHoursTemplate(hours: Int): String =
          context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)

    fun getMinutesTemplate(minutes: Int): String =
          context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)

    fun getHoursTemplateShort(hours: Int): String =
          context.resources.getString(R.string.stats_time_format_hours_short, hours)

    fun getMinutesTemplateShort(minutes: Int): String =
          context.resources.getString(R.string.stats_time_format_minutes_short, minutes)

    fun getDateRangeWithSeparator(start: String, end: String) =
          context.getString(R.string.summary_range_separator, start, end)

    fun getHumanReadableDateRange(dateRange: DateRange.PredefinedRange): String? = when (dateRange) {
        Today -> R.string.summary_date_today
        else -> null
    }?.let { context.resources.getString(it) }

}
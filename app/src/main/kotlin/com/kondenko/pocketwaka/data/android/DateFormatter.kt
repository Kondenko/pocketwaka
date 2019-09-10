package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.text.format.DateUtils
import com.kondenko.pocketwaka.utils.extensions.getCurrentLocale
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateFormatter(private val context: Context, private val stringProvider: StringProvider) {

    fun formatDateAsParameter(date: Date): String =
            SimpleDateFormat("yyyy-MM-dd").format(date.time)

    fun formatDateForDisplay(date: String): String {
        val locale = context.getCurrentLocale()
        val dateMillis = SimpleDateFormat("yyyy-MM-dd", locale).parse(date).time
        return DateUtils.formatDateTime(
                context,
                dateMillis,
                DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
        )
    }

    fun formatDateForDisplay(seconds: Int): String {
        return DateUtils.formatDateTime(
                context,
                TimeUnit.SECONDS.toMillis(seconds.toLong()),
                DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
        )
    }

    fun secondsToHumanReadableTime(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hours)
        val templateHours = stringProvider.getHoursTemplate(hours.toInt())
        val templateMinutes = stringProvider.getMinutesTemplate(minutes.toInt())
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }

}
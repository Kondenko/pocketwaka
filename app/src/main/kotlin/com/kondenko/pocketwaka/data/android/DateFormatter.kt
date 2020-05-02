package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.text.format.DateUtils
import com.kondenko.pocketwaka.utils.extensions.getCurrentLocale
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateFormatter(private val context: Context, private val stringProvider: StringProvider) {

    private val paramDateFormat = SimpleDateFormat("yyyy-MM-dd")

    enum class Format {
        Short, Long
    }

    fun formatDateAsParameter(date: Date): String =
          paramDateFormat.format(date.time)

    fun parseDateParameter(date: String): Long? {
        return try {
            paramDateFormat.parse(date).time
        } catch (e: ParseException) {
            Timber.w("Couldn't parse date $date")
            null
        }
    }

    fun formatDateForDisplay(date: String): String {
        val locale = context.getCurrentLocale()
        val dateMillis = SimpleDateFormat("yyyy-MM-dd", locale).parse(date).time
        return DateUtils.formatDateTime(
              context,
              dateMillis,
              DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
        )
    }

    fun formatDateForDisplay(seconds: Int, includeYear: Boolean = true): String {
        return DateUtils.formatDateTime(
              context,
              TimeUnit.SECONDS.toMillis(seconds.toLong()),
              (if (includeYear) DateUtils.FORMAT_SHOW_YEAR else DateUtils.FORMAT_NO_YEAR) or DateUtils.FORMAT_ABBREV_MONTH
        )
    }

    /**
     * A shorthand version of [secondsToHumanReadableTime] which uses [Format.Long] by default.
     * This overload is necessary to keep supporting method references to this method.
     */
    fun secondsToHumanReadableTime(seconds: Long): String = secondsToHumanReadableTime(seconds, Format.Long)

    fun secondsToHumanReadableTime(seconds: Long, format: Format = Format.Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(hours)
        return toHumanReadableTime(hours.toInt(), minutes.toInt(), format)
    }

    fun toHumanReadableTime(hours: Int, minutes: Int, format: Format = Format.Long): String {
        val templateHours = when (format) {
            Format.Long -> stringProvider.getHoursTemplate(hours)
            Format.Short -> stringProvider.getHoursTemplateShort(hours)
        }
        val templateMinutes = when (format) {
            Format.Long -> stringProvider.getMinutesTemplate(minutes)
            Format.Short -> stringProvider.getMinutesTemplateShort(minutes)
        }
        val timeBuilder = StringBuilder()
        if (hours > 0) timeBuilder.append(templateHours.format(hours)).append(' ')
        if (minutes > 0) timeBuilder.append(templateMinutes.format(minutes))
        return timeBuilder.toString()
    }

}
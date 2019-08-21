package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.text.format.DateUtils
import com.kondenko.pocketwaka.utils.extensions.getCurrentLocale
import java.text.SimpleDateFormat
import java.util.*

class DateFormatter(private val context: Context) {

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

}
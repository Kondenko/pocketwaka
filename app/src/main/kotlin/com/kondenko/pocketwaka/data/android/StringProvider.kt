package com.kondenko.pocketwaka.data.android

import android.content.Context
import com.kondenko.pocketwaka.R

class StringProvider(private val context: Context) {

    fun getSupportEmailSubject(): String =
            context.getString(R.string.menu_support_email_subject)

    fun getHoursTemplate(hours: Int): String =
            context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)

    fun getHoursTemplateShort(hours: Int): String =
            context.resources.getString(R.string.stats_time_format_hours_short, hours)

    fun getMinutesTemplate(minutes: Int): String =
            context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)

    fun getMinutesTemplateShort(hours: Int): String =
            context.resources.getString(R.string.stats_time_format_minutes_short, hours)

}
package com.kondenko.pocketwaka.utils

import android.content.Context
import com.kondenko.pocketwaka.R

class StringProvider(private val context: Context) {

    fun getHoursTemplate(hours: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)
    }

    fun getMinutesTemplate(minutes: Int): String {
        return context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)
    }

}
package com.kondenko.pocketwaka.utils.date

import java.util.*
import java.util.concurrent.TimeUnit

class TimeProvider {

    fun getCurrentTimeMillis() = System.currentTimeMillis()

    fun getCurrentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toFloat()

    /**
     * Formats the current day ISO 8601 UTC datetime
     */
    fun getDayAsString(date: Long): String {
        return Date(date).toString()
    }

    fun getToday(): Date {
        return Date()
    }

}
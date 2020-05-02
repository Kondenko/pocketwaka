package com.kondenko.pocketwaka.utils.date

import com.kondenko.pocketwaka.utils.extensions.roundDateToDay
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.days

class DateProvider {

    fun getCurrentTimeMillis() = System.currentTimeMillis()

    fun getCurrentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMillis()).toFloat()

    fun getToday() = Date().time.roundDateToDay()

    fun getYear(date: Date = Date()) = Calendar.getInstance().run {
        time = date
        get(Calendar.YEAR)
    }

}
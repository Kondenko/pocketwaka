package com.kondenko.pocketwaka.utils.date

import com.kondenko.pocketwaka.utils.extensions.roundDateToDay
import org.threeten.bp.DayOfWeek
import org.threeten.bp.MonthDay
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.days

class DateProvider {

    fun getCurrentTimeMillis() = System.currentTimeMillis()

    fun getCurrentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMillis()).toFloat()

    fun getToday() = MonthDay.now().atYear(Year.now().value)

    fun getYear(date: Date = Date()) = YearMonth.now().year

}
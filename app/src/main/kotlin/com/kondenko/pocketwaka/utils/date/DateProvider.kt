package com.kondenko.pocketwaka.utils.date

import org.threeten.bp.LocalDate
import org.threeten.bp.MonthDay
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import java.util.concurrent.TimeUnit

class DateProvider {

    val year: Int
        get() = YearMonth.now().year

    val today: LocalDate
        get() = MonthDay.now().atYear(Year.now().value)

    fun getCurrentTimeMillis() = System.currentTimeMillis()

    fun getCurrentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(getCurrentTimeMillis()).toFloat()

}
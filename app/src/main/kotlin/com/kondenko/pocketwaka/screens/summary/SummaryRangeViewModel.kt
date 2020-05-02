package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.roundDateToDay
import java.util.*
import java.util.concurrent.TimeUnit

class SummaryRangeViewModel(private val dateProvider: DateProvider) : ViewModel() {

    private val initialItem = DateRange.SingleDay(dateProvider.getToday().time.roundDateToDay())

    private val day = TimeUnit.DAYS.toMillis(1)

    private val dates = MutableLiveData<List<DateRange>>()

    init {
        loadAround(initialItem.date)
    }

    fun dateChanges(): LiveData<List<DateRange>> = dates

    fun loadAround(date: Long) {
        // TODO Limit scrolling by two weeks for free accounts
        val currentValue = dates.value
        if (currentValue == null || date != initialItem.date) {
            WakaLog.d("Loading items around ${Date(date)}")
            val newDates = if (initialItem.date == date) {
                listOf((date - day * 2L), (date - day), date)
            } else {
                listOf((date - day), date, (date + day))
            }.map { DateRange.SingleDay(it) }
            dates.postValue(newDates)
        }
    }

}
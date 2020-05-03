package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kondenko.pocketwaka.data.android.HumanReadableDateFormatter
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import java.util.*
import java.util.concurrent.TimeUnit

class SummaryRangeViewModel(dateProvider: DateProvider, private val dateFormatter: HumanReadableDateFormatter) : ViewModel() {

    private val today = DateRange.SingleDay(dateProvider.getToday())

    private val day = TimeUnit.DAYS.toMillis(1)

    private val dates = MutableLiveData<SummaryRangeState>()

    private val titles = MutableLiveData<String>()

    init {
        onDateScreenOpen(today)
    }

    fun dateChanges(): LiveData<SummaryRangeState> = dates

    fun titleChanges(): LiveData<String> = titles

    fun onDateScreenOpen(dateRange: DateRange) {
        // TODO Limit scrolling by two weeks for free accounts
        titles.value = dateFormatter.format(dateRange)
        if (dateRange is DateRange.SingleDay) dateRange.load()
        else TODO("Load ranges")
    }

    private fun DateRange.SingleDay.load() {
        val currentValue = dates.value
        if (currentValue == null || date != today.date) {
            WakaLog.d("Loading items around ${Date(date)}")
            val newDates = if (today.date == date) {
                listOf((date - day * 2L), (date - day), date)
            } else {
                listOf((date - day), date, (date + day))
            }
                  .map { DateRange.SingleDay(it) }
                  .let { SummaryRangeState(it) }
            dates.postValue(newDates)
        }
    }

}
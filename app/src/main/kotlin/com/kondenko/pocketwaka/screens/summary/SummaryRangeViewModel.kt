package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kizitonwose.calendarview.model.CalendarDay
import com.kondenko.pocketwaka.DaySelectionState
import com.kondenko.pocketwaka.data.android.HumanReadableDateFormatter
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.notNull
import org.threeten.bp.LocalDate

class SummaryRangeViewModel(dateProvider: DateProvider, private val dateFormatter: HumanReadableDateFormatter) : ViewModel() {

    // Events

    private val selectedRange = MutableLiveData<SummaryRangeState>()

    private val titles = MutableLiveData<String>()

    private val calendarInvalidation = MutableLiveData<Unit>()

    // Dates

    private val today = DateRange.SingleDay(dateProvider.getToday())

    private var startDate: LocalDate? = null

    private var endDate: LocalDate? = null

    init {
        selectDate(today)
    }

    fun dateChanges(): LiveData<SummaryRangeState> = selectedRange

    fun titleChanges(): LiveData<String> = titles

    fun calendarInvalidationEvents(): LiveData<Unit> = calendarInvalidation

    fun selectDate(dateRange: DateRange) {
        // TODO Limit scrolling by two weeks for free accounts
        titles.value = dateFormatter.format(dateRange)
        when(dateRange) {
            is DateRange.SingleDay -> dateRange.loadAdjacentDays()
            is DateRange.Range -> dateRange.loadRange()
        }
        calendarInvalidation.value = Unit
    }

    fun onDayClicked(day: CalendarDay) {
        WakaLog.d("Day selected: $day")
        val date = day.date
        if (startDate != null) {
            if (date < startDate || endDate != null) {
                startDate = date
                endDate = null
            } else if (date != startDate) {
                endDate = date
            } else {
                startDate = null
            }
        } else {
            startDate = date
        }
        calendarInvalidation.value = Unit
        WakaLog.d("Start date: $startDate")
        WakaLog.d("End date: $endDate")
    }

    fun confirmDateSelection() {
        val startDate = startDate
        val endDate = endDate
        if (startDate == null && endDate == null) return
        val selection = when {
            (startDate != null) xor (endDate != null) -> {
                DateRange.SingleDay(
                      startDate
                            ?: endDate
                            ?: throw IllegalArgumentException("One of dates is null")
                )
            }
            startDate != null && endDate != null -> {
                DateRange.Range(startDate, endDate)
            }
            else -> {
                throw IllegalArgumentException("Both start date and end date are null")
            }
        }
        selectDate(selection)
        calendarInvalidation.value = Unit
    }

    fun getSelectionState(day: CalendarDay) = when {
        day.date == startDate -> {
            if (endDate != null) DaySelectionState.Start else DaySelectionState.Single
        }
        day.date == endDate -> {
            DaySelectionState.End
        }
        notNull(startDate, endDate) && day.date.isAfter(startDate) && day.date.isBefore(endDate) -> {
            DaySelectionState.Middle
        }
        else -> {
            DaySelectionState.Unselected
        }
    }.also { if (it != DaySelectionState.Unselected) WakaLog.d("$day selection state: $it") }

    private fun DateRange.Range.loadRange() {
        selectedRange.value = SummaryRangeState(listOf(this))
    }

    private fun DateRange.SingleDay.loadAdjacentDays() {
        val currentValue = selectedRange.value
        if (currentValue == null || date != today.date) {
            WakaLog.d("Loading items around $date")
            val newDates = if (today.date == date) {
                listOf(date.minusDays(2), date.minusDays(1), date)
            } else {
                listOf(date.minusDays(1), date, date.plusDays(1))
            }
                  .map { DateRange.SingleDay(it) }
                  .let { SummaryRangeState(it) }
            selectedRange.postValue(newDates)
        }
    }

}
package com.kondenko.pocketwaka.screens.summary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kizitonwose.calendarview.model.CalendarDay
import com.kondenko.pocketwaka.data.android.HumanReadableDateFormatter
import com.kondenko.pocketwaka.domain.summary.model.AvailableRange
import com.kondenko.pocketwaka.domain.summary.usecase.GetAvailableRange
import com.kondenko.pocketwaka.screens.FragmentDatePicker.DaySelectionState
import com.kondenko.pocketwaka.screens.base.BaseViewModel
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.notNull
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import org.threeten.bp.LocalDate

class SummaryRangeViewModel(
      dateProvider: DateProvider,
      private val dateFormatter: HumanReadableDateFormatter,
      private val getAvailableRange: GetAvailableRange
) : BaseViewModel<Nothing>() {

    // Events

    private val availableRange = MutableLiveData<AvailableRange>()

    private val selectedRange = MutableLiveData<SummaryRangeState>()

    private val titles = MutableLiveData<String>()

    private val calendarInvalidationEvents = MutableLiveData<Unit>()

    private val closeEvents = MutableLiveData<Unit>()

    // Dates

    private val today = DateRange.SingleDay(dateProvider.getToday())

    private var startDate: LocalDate? = null

    private var endDate: LocalDate? = null

    private val adjacentDatesNumber = 0 // STOPSHIP TODO Change to 2

    init {
        disposables += getAvailableRange.build()
              //.map { AvailableRange.Unlimited } // STOPSHIP TODO Remove
              .doOnSuccess { selectDate(today) }
              .subscribeBy(onSuccess = availableRange::postValue, onError = WakaLog::e)
    }

    fun availableRangeChanges(): LiveData<AvailableRange> = availableRange

    fun dateChanges(): LiveData<SummaryRangeState> = selectedRange

    fun titleChanges(): LiveData<String> = titles

    fun calendarInvalidationEvents(): LiveData<Unit> = calendarInvalidationEvents

    fun closeEvents(): LiveData<Unit> = closeEvents

    fun onButtonClicked(dateRange: DateRange) {
        startDate = null
        endDate = null
        selectDate(dateRange)
        calendarInvalidationEvents.value = Unit
        closeEvents.value = Unit
    }

    fun selectDate(dateRange: DateRange) {
        // TODO Limit scrolling by two weeks for free accounts
        titles.value = dateFormatter.format(dateRange)
        when (dateRange) {
            is DateRange.SingleDay -> dateRange.loadAdjacentDays()
            is DateRange.Range -> dateRange.loadRange()
        }
        calendarInvalidationEvents.value = Unit
    }

    fun onDayClicked(day: CalendarDay) {
        val date = day.date
        // TODO Allow selecting a date before start date (so the backwards selection order is also possible)
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
        calendarInvalidationEvents.value = Unit
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
        calendarInvalidationEvents.value = Unit
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
    }

    private fun DateRange.Range.loadRange() {
        selectedRange.value = SummaryRangeState(listOf(this))
    }

    private fun DateRange.SingleDay.loadAdjacentDays() {
        val currentValue = selectedRange.value
        if (currentValue == null || date != today.date) {
            val newDates = if (today.date == date) {
                (adjacentDatesNumber downTo 0L).map(date::minusDays)
            } else {
                val daysOnEachSide = adjacentDatesNumber / 2
                val range = (1L..daysOnEachSide)
                range.reversed().map(date::minusDays) + listOf(date) + range.map(date::plusDays)
            }
                  .map { DateRange.SingleDay(it) }
                  .let { SummaryRangeState(it) }
            selectedRange.value = newDates
        }
    }

}
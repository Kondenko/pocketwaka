package com.kondenko.pocketwaka.domain.summary.model

import com.kondenko.pocketwaka.utils.date.DateRange

sealed class AvailableRange {

    object Unlimited : AvailableRange()

    data class Limited(val date: DateRange) : AvailableRange()

    object Unknown : AvailableRange()

}
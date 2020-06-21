package com.kondenko.pocketwaka.screens.summary

import com.kondenko.pocketwaka.utils.date.DateRange

data class SummaryRangeState(val invalidateScreens: Boolean, val dates: List<DateRange>)
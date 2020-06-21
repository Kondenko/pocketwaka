package com.kondenko.pocketwaka.screens.summary

import com.kondenko.pocketwaka.utils.date.DateRange

data class SummaryRangeState(
      val dates: List<DateRange>,
      val invalidateScreens: Boolean,
      val openLastItem: Boolean = false
)
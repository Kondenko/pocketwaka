package com.kondenko.pocketwaka.data.stats.repository

import android.content.Context
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.stats.service.StatsService
import javax.inject.Inject

@PerScreen
class StatsRepository @Inject constructor(private val context: Context, private val service: StatsService) {

    fun getStats(tokenHeader: String, range: String) = service.getCurrentUserStats(tokenHeader, range)

    fun getBestDayHoursTemplate(hours: Int): String = context.resources.getQuantityString(R.plurals.stats_time_format_hours, hours)

    fun getBestDayMinutesTemplate(minutes: Int): String = context.resources.getQuantityString(R.plurals.stats_time_format_minutes, minutes)

}
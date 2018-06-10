package com.kondenko.pocketwaka.data.stats.repository

import com.kondenko.pocketwaka.dagger.PerScreen
import com.kondenko.pocketwaka.data.stats.service.StatsService
import javax.inject.Inject

@PerScreen
class StatsRepository @Inject constructor(private val service: StatsService) {

    fun getStats(tokenHeader: String, range: String) = service.getCurrentUserStats(tokenHeader, range)

}
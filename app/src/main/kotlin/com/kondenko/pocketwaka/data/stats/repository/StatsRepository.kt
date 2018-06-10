package com.kondenko.pocketwaka.data.stats.repository

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.stats.service.StatsService
import javax.inject.Inject

@PerApp
class StatsRepository @Inject constructor(private val service: StatsService) {

    fun getStats(tokenHeader: String, range: String) = service.getCurrentUserStats(tokenHeader, range)

}
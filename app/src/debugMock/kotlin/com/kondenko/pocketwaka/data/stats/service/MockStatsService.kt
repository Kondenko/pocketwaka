package com.kondenko.pocketwaka.data.stats.service

import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle

class MockStatsService : StatsService {

    override fun getCurrentUserStats(tokenHeaderValue: String, range: String): Single<StatsDataWrapper> {
        return StatsDataWrapper().toSingle() // TODO Replace with a parsed json
    }

}
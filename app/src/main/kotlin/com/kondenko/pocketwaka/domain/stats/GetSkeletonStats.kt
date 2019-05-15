package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.stats.model.BestDay
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.SchedulerContainer
import com.kondenko.pocketwaka.utils.times
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle


class GetSkeletonStats(schedulers: SchedulerContainer) : UseCaseSingle<Nothing, StatsModel>(schedulers) {

    private val skeletonStatsCard = mutableListOf(StatsItem(null, null, null, null)) * 3

    override fun build(params: Nothing?): Single<StatsModel> = StatsModel(
            bestDay = BestDay("", "", 0),
            humanReadableDailyAverage = "",
            humanReadableTotal = "",
            projects = skeletonStatsCard,
            languages = skeletonStatsCard,
            editors = null,
            operatingSystems = null,
            range = "",
            lastUpdated = 0,
            isEmpty = false
    ).toSingle()

}
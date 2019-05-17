package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.times
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle


class GetSkeletonPlaceholderData(schedulers: SchedulersContainer) : UseCaseSingle<Nothing, List<StatsModel>>(schedulers) {

    private val skeletonStatsCard = mutableListOf(StatsItem(null, null, null, null)) * 3

    override fun build(params: Nothing?): Single<List<StatsModel>> = listOf(
            StatsModel.Info(null, null),
            StatsModel.BestDay("", "", 0),
            StatsModel.Stats(skeletonStatsCard),
            StatsModel.Stats(skeletonStatsCard)
    ).toSingle()

}
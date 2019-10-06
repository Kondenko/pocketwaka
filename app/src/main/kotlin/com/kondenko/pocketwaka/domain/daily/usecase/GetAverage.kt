package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.domain.UseCaseMaybe
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Maybe

class GetAverage(
        schedulersContainer: SchedulersContainer,
        private val rangeStatsRepository: RangeStatsRepository,
        private val getTokenHeaderValue: GetTokenHeaderValue
) : UseCaseMaybe<StatsRange, Int>(schedulersContainer) {

    override fun build(params: StatsRange?): Maybe<Int> =
            params?.let { range ->
                getTokenHeaderValue.build().flatMapMaybe { token ->
                    rangeStatsRepository.getDailyAverage(token, range)
                }
            } ?: Maybe.error(NullPointerException("Stats range is null"))
}
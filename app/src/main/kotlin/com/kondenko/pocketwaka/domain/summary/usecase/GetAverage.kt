package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseMaybe
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Maybe

class GetAverage(
      schedulersContainer: SchedulersContainer,
      private val statsRepository: StatsRepository,
      private val getTokenHeaderValue: GetTokenHeaderValue
) : UseCaseMaybe<StatsRange, Int>(schedulersContainer) {

    override fun build(params: StatsRange?): Maybe<Int> =
            params?.let { range ->
                getTokenHeaderValue.build().flatMapMaybe { token ->
                    statsRepository.getDailyAverage(token, range)
                }
            } ?: Maybe.error(NullPointerException("Stats range is null"))
}
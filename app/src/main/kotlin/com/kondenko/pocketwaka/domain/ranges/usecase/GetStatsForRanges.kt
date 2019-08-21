package com.kondenko.pocketwaka.domain.ranges.usecase

import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.data.ranges.repository.StatsRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable


class GetStatsForRanges(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<GetStatsForRanges.Params, StatsDto>(schedulers) {

    class Params(val range: String?, refreshRate: Int = 1, retryAttempts: Int = 3) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = range != null
    }

    override fun build(params: Params?): Observable<StatsDto> =
            getTokenHeader.build().flatMapObservable { header ->
                statsRepository.getData(StatsRepository.Params(header, params!!.range!!))
            }

}
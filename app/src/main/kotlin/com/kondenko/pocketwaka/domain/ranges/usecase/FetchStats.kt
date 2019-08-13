package com.kondenko.pocketwaka.domain.ranges.usecase

import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.data.ranges.repository.StatsRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable


class FetchStats(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<FetchStats.Params, StatsDto>(schedulers) {

    class Params(val range: String?, refreshRate: Int) : StatefulUseCase.ParamsWrapper(refreshRate) {
        override fun isValid(): Boolean = range != null
    }

    override fun build(params: Params?): Observable<StatsDto> =
            getTokenHeader.build().flatMapObservable { header ->
                statsRepository.getData(StatsRepository.Params(header, params!!.range!!))
            }

}
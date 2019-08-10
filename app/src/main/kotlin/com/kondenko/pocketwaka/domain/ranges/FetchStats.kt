package com.kondenko.pocketwaka.domain.ranges

import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.data.ranges.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable


class FetchStats(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<String, StatsDto>(schedulers) {

    override fun build(range: String?): Observable<StatsDto> =
            getTokenHeader.build().flatMapObservable { header ->
                statsRepository.getStats(header, range!!)
            }

}
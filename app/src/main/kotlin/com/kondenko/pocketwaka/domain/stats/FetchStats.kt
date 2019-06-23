package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable


class FetchStats(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseObservable<String, List<StatsModel>>(schedulers) {

    override fun build(range: String?): Observable<List<StatsModel>> {
        return getTokenHeader.build()
                .flatMapObservable { header ->
                    statsRepository.getStats(header, range!!)
                }
                .map { dto ->
                    dto.stats.map {
                        if (it is StatsModel.Metadata) it.copy(isFromCache = dto.isFromCache)
                        else it
                    }
                }

    }

}
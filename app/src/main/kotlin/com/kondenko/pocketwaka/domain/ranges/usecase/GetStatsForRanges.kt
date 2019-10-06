package com.kondenko.pocketwaka.domain.ranges.usecase

import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Maybe
import io.reactivex.Observable


class GetStatsForRanges(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val rangeStatsRepository: RangeStatsRepository,
        private val serverModelConverter: (RangeStatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel>
) : UseCaseObservable<GetStatsForRanges.Params, StatsDbModel>(schedulers) {

    class Params(val range: String?, refreshRate: Int = 1, retryAttempts: Int = 3) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = range != null
    }

    override fun build(params: Params?): Observable<StatsDbModel> =
            getTokenHeader.build().flatMapObservable { header ->
                rangeStatsRepository.getData(RangeStatsRepository.Params(header, params!!.range!!)) { params ->
                    flatMap {
                        serverModelConverter(params, it).toSingle()
                    }
                }
            }

}
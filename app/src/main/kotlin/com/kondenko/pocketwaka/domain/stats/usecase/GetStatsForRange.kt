package com.kondenko.pocketwaka.domain.stats.usecase

import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.stats.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Maybe
import io.reactivex.Observable


class GetStatsForRange(
      schedulers: SchedulersContainer,
      private val getTokenHeader: GetTokenHeaderValue,
      private val statsRepository: StatsRepository,
      private val serverModelConverter: (StatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel>
) : UseCaseObservable<GetStatsForRange.Params, StatsDbModel>(schedulers) {

    class Params(val range: String?, refreshRate: Int = 1, retryAttempts: Int = 3) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = range != null
    }

    override fun build(params: Params?): Observable<StatsDbModel> =
            getTokenHeader.build().flatMapObservable { header ->
                statsRepository.getData(StatsRepository.Params(header, params!!.range!!)) { params ->
                    flatMap {
                        serverModelConverter(params, it).toSingle()
                    }
                }
            }

}
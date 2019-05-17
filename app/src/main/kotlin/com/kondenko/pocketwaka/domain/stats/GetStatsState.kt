package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.ErrorType
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GetStatsState(
        private val schedulers: SchedulersContainer,
        private val getSkeletonPlaceholderData: GetSkeletonPlaceholderData,
        private val fetchStats: FetchStats
) : UseCaseObservable<GetStatsState.Params, State<StatsModel>>(schedulers) {

    data class Params(val range: String, val refreshRateMin: Int)

    override fun build(params: Params?): Observable<State<StatsModel>> {
        if (params == null) return Observable.error(IllegalArgumentException("Params shouldn't be null"))
        val loading = getSkeletonPlaceholderData.build().toObservable().map { State.Loading(it) }
        val data = Observable.interval(0, params.refreshRateMin.toLong(), TimeUnit.MINUTES, schedulers.workerScheduler)
                .flatMap {
                    fetchStats.build(params.range)
                            .toObservable()
                            .map<State<StatsModel>> { State.Success(it) }
                }
        return Observable.concat(loading, data)
                .onErrorReturn { State.Failure(ErrorType.Unknown(it)) }
                .doOnNext { Timber.d("Stats state updated: ${it.javaClass}") }
    }

}
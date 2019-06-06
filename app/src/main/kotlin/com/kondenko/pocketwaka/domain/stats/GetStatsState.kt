package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.ErrorType
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class GetStatsState(
        private val schedulers: SchedulersContainer,
        private val getSkeletonPlaceholderData: GetSkeletonPlaceholderData,
        private val fetchStats: FetchStats
) : UseCaseObservable<GetStatsState.Params, State<List<StatsModel>>>(schedulers) {

    private val delayBetweenSkeletonAndActualUi: Long = 50

    data class Params(val range: String?, val refreshRateMin: Int)

    override fun build(params: Params?): Observable<State<List<StatsModel>>> {
        if (params == null) return Observable.just(State.Failure(ErrorType.UnknownRange))
        val initialDelay = Observable.timer(delayBetweenSkeletonAndActualUi, TimeUnit.MILLISECONDS)
        val loading = getSkeletonPlaceholderData.build()
                .toObservable()
                .map { State.Loading(it) }
                .flatMap { state ->
                    /*
                    When the user tries to refresh the data it refreshes so quickly
                    that the loading screen doesn't have the time to be shown and hidden again.
                    We add a tiny delay to the loading process so users
                    actually see that the loading happens.
                     */
                    initialDelay.map { state }
                }
        val data = Observable.interval(0, params.refreshRateMin.toLong(), TimeUnit.MINUTES, schedulers.workerScheduler)
                .flatMap {
                    fetchStats.build(params.range)
                            .map<State<List<StatsModel>>> { State.Success(it) }
                }
        return Observable.concat(loading, data)
                .onErrorReturn { State.Failure(ErrorType.Unknown(it)) }
    }

}
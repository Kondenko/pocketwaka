package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.ErrorType
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.flatMapLatest
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

private typealias StatsState = State<List<StatsModel>>

class GetStatsState(
        private val schedulers: SchedulersContainer,
        private val getSkeletonPlaceholderData: GetSkeletonPlaceholderData,
        private val fetchStats: FetchStats,
        private val connectivityStatusProvider: ConnectivityStatusProvider
) : UseCaseObservable<GetStatsState.Params, StatsState>(schedulers) {

    /*
        When the user tries to refresh the data it refreshes so quickly
        that the loading screen doesn't have the time to be shown and hidden again.
        We add a tiny delay to the loading process so users
        actually see that the loading happens.
     */
    private val delayBetweenSkeletonAndActualUi: Long = 75

    data class Params(val range: String?, val refreshRateMin: Int)

    override fun build(params: Params?): Observable<StatsState> {
        if (params == null) return Observable.just(State.Failure(ErrorType.UnknownRange))
        val initialDelay = Observable.timer(delayBetweenSkeletonAndActualUi, TimeUnit.MILLISECONDS)
        val loading = getSkeletonPlaceholderData.build()
                .toObservable()
                .map { State.Loading(it) }
                .flatMap { state ->
                    initialDelay.map { state }
                }
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val stats = fetchStats.build(params.range).map<StatsState> { State.Success(it) }
        val interval = Observable.interval(0, params.refreshRateMin.toLong(), TimeUnit.MINUTES, schedulers.workerScheduler)
        return flatMapLatest(connectivityStatus, interval) { isConnected, _ ->
            if (isConnected) stats
            else Observable.just(State.Failure(ErrorType.NoNetwork))
        }.startWith(loading)
    }

}
package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.screens.base.State.*
import com.kondenko.pocketwaka.screens.base.copyFrom
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.flatMapLatest
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


private typealias StatsModelList = List<StatsModel>
private typealias StatsState = State<StatsModelList>

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
        if (params == null) return Observable.just(Failure.UnknownRange<StatsModelList>())

        fun stats() = fetchStats.build(params.range).map<StatsState> { Success(it) }

        val initialDelay = Observable.timer(delayBetweenSkeletonAndActualUi, TimeUnit.MILLISECONDS)
        val loading = getSkeletonPlaceholderData.build()
                .toObservable()
                .map { Loading(it) }
                .flatMap { state ->
                    initialDelay.map { state }
                }
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val interval = Observable.interval(0, params.refreshRateMin.toLong(), TimeUnit.MINUTES, schedulers.workerScheduler)

        return flatMapLatest(connectivityStatus, interval) { isConnected, _ ->
            if (isConnected) {
                stats().onErrorReturn { t: Throwable ->
                    Failure.Unknown<StatsState>(exception = t)
                }
            } else {
                Observable.just(Failure.NoNetwork<StatsState>())
            }
        }
                .startWith(loading)
                .scan { old: StatsState, new: StatsState ->
                    when {
                        old is Success<StatsModelList> -> {
                            when (new) {
                                is Failure.NoNetwork<*> -> Offline(old.data)
                                is Failure<*> -> new.copyFrom(old.data, new.exception)
                                else -> new
                            }
                        }
                        old is Offline<StatsModelList> && new is Failure<*> -> old
                        old is Failure<*> && new is Failure<*> -> {
                            new.copyFrom(old.data)
                        }
                        else -> new
                    }
                }
                .distinctUntilChanged { o, n ->
                    if (o is Failure<*> && n is Failure<*>) o.exception == n.exception
                    else o == n
                }
    }


}
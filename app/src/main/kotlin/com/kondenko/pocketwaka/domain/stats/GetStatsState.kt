package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.screens.base.State.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
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

    data class Params(val range: String?, val refreshRateMin: Int, val retryAttempts: Int)

    override fun build(params: Params?): Observable<StatsState> {
        if (params?.range == null) return Observable.just(Failure.UnknownRange())
        val initialDelay = Observable.timer(delayBetweenSkeletonAndActualUi, TimeUnit.MILLISECONDS)
        val loading = getSkeletonPlaceholderData.build()
                .map { Loading(skeletonData = it) }
                .flatMap { state ->
                    initialDelay.map { state }
                }
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val interval = Observable.interval(
                0,
                params.refreshRateMin.toLong(),
                TimeUnit.MINUTES,
                schedulers.workerScheduler
        )
        return connectivityStatus
                .switchMapDelayError { isConnected ->
                    interval.flatMap {
                        Observable.concatArray(loading, getStats(params.range, params.retryAttempts.toLong(), isConnected))
                    }
                }
                .scan(::changeState)
                .distinctUntilChanged(::equal)
    }

    private fun getStats(range: String, retryAttempts: Long, isConnected: Boolean) =
            fetchStats.build(range)
                    .map {
                        val stats = it.stats
                        if (it.isFromCache) {
                            if (isConnected) Loading(stats, emptyList(), false)
                            else Offline(stats)
                        } else {
                            Success(stats)
                        }
                    }
                    .onErrorReturn { t ->
                        if (isConnected) Failure.Unknown(exception = t)
                        else Failure.NoNetwork(exception = t)
                    }

    private fun equal(old: StatsState, new: StatsState) = when {
        old is Failure<StatsModelList> && new is Failure<StatsModelList> -> old.exception == new.exception
        else -> old == new
    }

    private fun changeState(old: StatsState, new: StatsState): StatsState = when {
        new is Loading<StatsModelList> -> {
            if (old is Success) new.copy(data = old.data, isInterrupting = false)
            else new.copy(data = new.data ?: old.data, isInterrupting = new.data == null && old.data == null)
        }
        old is Loading<StatsModelList> -> {
            when (new) {
                is Failure.NoNetwork -> old.data?.let { Offline(data = it) } ?: new.copy(isFatal = true)
                is Failure.UnknownRange -> new.copy(data = old.data, isFatal = old.data == null)
                is Failure.Unknown -> new.copy(data = old.data, isFatal = old.data == null)
                is Loading -> old.data?.let { new.copy(data = it) } ?: new
                is Offline -> old.data?.let { new.copy(data = it) } ?: new
                else -> new
            }
        }
        else -> {
            new
        }
    }

}
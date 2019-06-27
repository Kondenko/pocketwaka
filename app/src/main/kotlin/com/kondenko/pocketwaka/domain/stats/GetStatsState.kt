package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.screens.base.State.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit


private typealias StatsModelList = List<StatsModel>
private typealias StatsState = State<StatsModelList>

class GetStatsState(
        private val schedulers: SchedulersContainer,
        private val fetchStats: FetchStats,
        private val connectivityStatusProvider: ConnectivityStatusProvider
) : UseCaseObservable<GetStatsState.Params, StatsState>(schedulers) {

    data class Params(val range: String?, val refreshRateMin: Int, val retryAttempts: Int)

    override fun build(params: Params?): Observable<StatsState> {
        if (params?.range == null) return Observable.just(Failure.UnknownRange())
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val interval = Observable.interval(
                0,
                params.refreshRateMin.toLong(),
                TimeUnit.MINUTES,
                schedulers.workerScheduler
        )
        val loading = Observable.just(Loading(null, true))
        return connectivityStatus
                .switchMapDelayError { isConnected ->
                    interval.flatMap {
                        val stats = getStats(params.range, params.retryAttempts.toLong(), isConnected)
                        Observable.concatArray(loading, stats)
                    }
                }
                .scan(::changeState)
                .distinctUntilChanged(::equal)
                .subscribeOn(schedulers.workerScheduler)
    }

    private fun getStats(range: String, retryAttempts: Long, isConnected: Boolean) =
            fetchStats.build(range)
                    .doOnError(Timber::w)
                    .retry(retryAttempts)
                    .map {
                        val stats = it.stats
                        if (it.isFromCache) {
                            if (isConnected) Loading(stats)
                            else Offline(stats)
                        } else {
                            if (it.isEmpty) Empty
                            else Success(stats)
                        }
                    }
                    .onErrorReturn { t ->
                        if (isConnected) Failure.Unknown(exception = t)
                        else Failure.NoNetwork(exception = t)
                    }
                    .subscribeOn(schedulers.workerScheduler)

    private fun equal(old: StatsState, new: StatsState) = when {
        old is Failure<StatsModelList> && new is Failure<StatsModelList> -> old.exception == new.exception
        else -> old == new
    }

    private fun changeState(old: StatsState, new: StatsState): StatsState = when {
        new is Loading<StatsModelList> -> {
            new.copy(new.data ?: old.data, old.data == null)
        }
        old is Loading<StatsModelList> -> {
            when (new) {
                is Failure.NoNetwork -> {
                    old.data?.let { Offline(data = it) } ?: new.copy(isFatal = true)
                }
                is Failure.UnknownRange -> {
                    new.copy(data = old.data, isFatal = old.data == null)
                }
                is Failure.Unknown -> {
                    new.copy(data = old.data, isFatal = old.data == null)
                }
                is Loading -> {
                    old.data?.let { new.copy(data = it) } ?: new
                }
                is Offline -> {
                    old.data?.let { new.copy(data = it) } ?: new
                }
                else -> {
                    new
                }
            }
        }
        else -> {
            new
        }
    }

}
package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.screens.base.State.*
import com.kondenko.pocketwaka.screens.base.copyFrom
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

    private val maxRetries = 3L


    data class Params(val range: String?, val refreshRateMin: Int)

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
                .switchMap { isConnected ->
                    interval
                            .flatMap<StatsState> {
                                if (isConnected) Observable.concatArray(loading, getStats(params.range, isConnected))
                                else Observable.just(Failure.NoNetwork())
                            }
                }
                .scan(::changeState)
                .distinctUntilChanged(::distinct)
    }

    private fun getStats(range: String, isConnected: Boolean) =
            fetchStats.build(range)
                    .map<StatsState> { Success(it) }
                    .retry(maxRetries)
                    .onErrorReturn { t ->
                        if (isConnected) Failure.Unknown(exception = t)
                        else Failure.NoNetwork(exception = t)
                    }

    private fun distinct(old: StatsState, new: StatsState) =
            if (old is Failure<StatsModelList> && new is Failure<StatsModelList>) old.exception == new.exception
            else old == new

    private fun changeState(old: StatsState, new: StatsState): StatsState = when {
        new is Loading<StatsModelList> -> {
            when (old) {
                is Success -> new.copy(data = old.data, isInterrupting = false)
                is Offline -> new.copy(data = old.data, isInterrupting = old.data == null)
                is Failure<*> -> new.copy(data = old.data, isInterrupting = old.data == null)
                else -> new
            }
        }
        old is Loading<StatsModelList> -> {
            when (new) {
                is Loading -> new.copy(data = old.data)
                is Offline -> new.copy(data = old.data)
                is Failure.NoNetwork -> new.copy(data = old.data)
                is Failure.UnknownRange -> new.copy(data = old.data)
                is Failure.Unknown -> new.copy(data = old.data)
                else -> new
            }
        }
        old is Success<StatsModelList> && new is Failure.NoNetwork<*> -> {
            Offline(old.data)
        }
        old is Success<StatsModelList> && new is Failure<StatsModelList> -> {
            new.copyFrom(old.data, new.exception)
        }
        old is Offline<StatsModelList> && new is Failure<*> -> {
            old
        }
        old is Failure<StatsModelList> && new is Failure<StatsModelList> -> {
            new.copyFrom(old.data)
        }
        else -> {
            new
        }
    }

}
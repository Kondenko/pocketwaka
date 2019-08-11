package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.State.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import timber.log.Timber
import java.util.concurrent.TimeUnit


abstract class StatefulUseCase<PARAMS : StatefulUseCase.Params, UI_MODEL, DOMAIN_MODEL : CacheableModel<UI_MODEL>>(
        private val schedulers: SchedulersContainer,
        private val useCaseSingle: UseCaseObservable<PARAMS, DOMAIN_MODEL>,
        private val connectivityStatusProvider: ConnectivityStatusProvider
) : UseCaseObservable<PARAMS, State<UI_MODEL>>(schedulers) {

    abstract class Params(val refreshRate: Long, val retryAttempts: Int) {
        abstract fun isValid(): Boolean
    }

    override fun build(params: PARAMS?): Observable<State<UI_MODEL>> {
        if (params?.isValid() == true) return Observable.just(Failure.InvalidParams())
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val interval = Observable.interval(
                0,
                params!!.refreshRate,
                TimeUnit.MINUTES,
                schedulers.workerScheduler
        )
        val loading = Observable.just(Loading(null, true))
        return connectivityStatus
                .switchMapDelayError { isConnected ->
                    interval.flatMap {
                        val data: Observable<State<UI_MODEL>> = getData(params, params.retryAttempts, isConnected)
                        Observable.concatArray(loading, data)
                    }
                }
                .scan(::changeState)
                .distinctUntilChanged(::equal)
                .subscribeOn(schedulers.workerScheduler)
    }

    private fun getData(params: PARAMS, retryAttempts: Int, isConnected: Boolean): Observable<State<UI_MODEL>> =
            useCaseSingle.build(params)
                    .doOnError(Timber::w)
                    .retry(retryAttempts.toLong())
                    .map {
                        val stats = it.data
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

    private fun equal(old: State<UI_MODEL>, new: State<UI_MODEL>) = when {
        old is Failure<*> && new is Failure<*> -> old.exception == new.exception
        else -> old == new
    }

    private fun changeState(old: State<UI_MODEL>, new: State<UI_MODEL>): State<UI_MODEL> = when {
        new is Loading<UI_MODEL> -> {
            new.copy(new.data ?: old.data, old.data == null)
        }
        old is Loading<UI_MODEL> -> {
            when (new) {
                is Failure.NoNetwork -> {
                    old.data?.let { Offline(data = it) } ?: new.copy(isFatal = true)
                }
                is Failure.InvalidParams -> {
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
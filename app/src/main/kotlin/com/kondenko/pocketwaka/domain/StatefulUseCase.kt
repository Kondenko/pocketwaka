package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.State.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import com.kondenko.pocketwaka.utils.extensions.returnAfterCompletion
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Fetches data and returns the appropriate state based on various conditions
 * (loading, error, empty etc.)
 *
 * @param modelMapper if the class stored in the database is not the class to be rendered, convert it
 *
 * @param UI_MODEL the class to be passed to a ViewModel and rendered
 * @param DATABASE_MODEL the class returned from a repository
 */
abstract class StatefulUseCase<
      PARAMS : StatefulUseCase.ParamsWrapper,
      UI_MODEL,
      DATABASE_MODEL : CacheableModel<UI_MODEL>
      >(
      private val schedulers: SchedulersContainer,
      private val dataProvider: UseCaseObservable<PARAMS, DATABASE_MODEL>,
      private val clearCache: UseCaseCompletable<Nothing>,
      private val connectivityStatusProvider: ConnectivityStatusProvider
) : UseCaseObservable<PARAMS, State<UI_MODEL>>(schedulers) {

    abstract class ParamsWrapper(open val refreshRate: Int, open val retryAttempts: Int, open val isPaged: Boolean) {
        abstract fun isValid(): Boolean
    }

    override fun build(params: PARAMS?): Observable<State<UI_MODEL>> {
        if (params?.isValid() == false) return Observable.just(Failure.InvalidParams())
        val connectivityStatus = connectivityStatusProvider.isNetworkAvailable()
        val interval = Observable.interval(
              0,
              params!!.refreshRate.toLong(),
              TimeUnit.MINUTES,
              schedulers.workerScheduler
        )
        val loading = Observable.just(Loading<UI_MODEL>(null, true))
        return connectivityStatus
              .switchMapDelayError { isConnected ->
                  interval.flatMap {
                      val data = getData(params, params.retryAttempts, isConnected, params.isPaged)
                      Observable.concatArray(loading, data)
                  }
              }
              .scan { old, new -> changeState(old, new, params.isPaged) }
              .distinctUntilChanged(::equal)
              .subscribeOn(schedulers.workerScheduler)
    }

    private fun getData(params: PARAMS, retryAttempts: Int, isConnected: Boolean, expectMoreData: Boolean): Observable<State<UI_MODEL>> =
          dataProvider.build(params)
                .retry(retryAttempts.toLong())
                .map { databaseModelToState(it, isConnected, expectMoreData) }
                .onErrorResumeNext { t: Throwable ->
                    when {
                        t is UnauthorizedException -> {
                            clearCache.build()
                                  .onErrorComplete()
                                  .andThen(Observable.just(Failure.Unauthorized(t)))
                        }
                        isConnected -> {
                            Observable.just(Failure.Unknown(exception = t))
                        }
                        else -> {
                            Observable.just(Failure.NoNetwork(exception = t))
                        }
                    }
                }
                .subscribeOn(schedulers.workerScheduler)
                .returnAfterCompletion<State<UI_MODEL>> {
                    if (it is Loading && it.data != null) {
                        Observable.just(Success(it.data
                              ?: throw NullPointerException("Unexpected null data in Loading")))
                    } else {
                        it?.let { Observable.just(it) } ?: Observable.empty()
                    }
                }

    protected open fun databaseModelToState(model: DATABASE_MODEL, isConnected: Boolean, expectMoreData: Boolean): State<UI_MODEL> {
        val uiModel = model.data
        return if (model.isFromCache) {
            if (isConnected) Loading(uiModel)
            else Offline(uiModel)
        } else {
            when {
                model.isEmpty == true -> Empty
                expectMoreData -> Loading(uiModel, isInterrupting = false)
                else -> Success(uiModel)
            }
        }
    }

    private fun equal(old: State<UI_MODEL>, new: State<UI_MODEL>) = when {
        old is Failure<*> && new is Failure<*> -> old.exception == new.exception
        else -> old == new
    }

    private fun changeState(old: State<UI_MODEL>, new: State<UI_MODEL>, expectMoreData: Boolean): State<UI_MODEL> = when {
        new is Loading<UI_MODEL> -> transitionToLoadingState(old, new, expectMoreData)
        old is Loading<UI_MODEL> -> transitionToNewState(old, new)
        else -> new
    }

    private fun transitionToLoadingState(old: State<UI_MODEL>, new: Loading<UI_MODEL>, expectMoreData: Boolean) = new.copy(
          new.data ?: old.data, // keep showing old data while displaying a loading indicator
          old.data == null && !expectMoreData // show a full-screen loading indicator if no data is present
    )

    private fun transitionToNewState(old: Loading<UI_MODEL>, new: State<UI_MODEL>) = when (new) {
        // If there was any data present, go to the new state while still showing the data
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
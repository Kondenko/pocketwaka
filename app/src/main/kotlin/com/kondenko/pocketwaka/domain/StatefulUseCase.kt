package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.data.CacheableModel
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.State.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

/**
 * Fetches data and returns the appropriate state based on various conditions
 * (loading, error, empty etc.)
 *
 * @param modelMapper if the class stored in the database is not the class to be rendered, convert it
 *
 * @param UI_MODEL the class to be passed to a ViewModel and rendered
 * @param INTERMEDIATE_MODEL the class stored in the database
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

    abstract class ParamsWrapper(open val refreshRate: Int, open val retryAttempts: Int) {
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
        val loading = Observable.just(Loading(null, true))
        return connectivityStatus
              .switchMapDelayError { isConnected ->
                  interval.flatMap {
                      val data = getData(params, params.retryAttempts, isConnected)
                      Observable.concatArray(loading, data)
                  }
              }
              .scan(::changeState)
              .distinctUntilChanged(::equal)
              .subscribeOn(schedulers.workerScheduler)
    }

    private fun getData(params: PARAMS, retryAttempts: Int, isConnected: Boolean): Observable<State<UI_MODEL>> =
          dataProvider.build(params)
                .retry(retryAttempts.toLong())
                .map { databaseModelToState(it, isConnected) }
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

    protected open fun databaseModelToState(model: DATABASE_MODEL, isConnected: Boolean): State<UI_MODEL> {
        val uiModel = model.data
        return if (model.isFromCache) {
            if (isConnected) Loading(uiModel)
            else Offline(uiModel)
        } else {
            if (model.isEmpty == true) Empty
            else Success(uiModel)
        }
    }

    private fun equal(old: State<UI_MODEL>, new: State<UI_MODEL>) = when {
        old is Failure<*> && new is Failure<*> -> old.exception == new.exception
        else -> old == new
    }

    private fun changeState(old: State<UI_MODEL>, new: State<UI_MODEL>): State<UI_MODEL> = when {
        new is Loading<UI_MODEL> -> transitionToLoadingState(old, new)
        old is Loading<UI_MODEL> -> transitionToNewState(old, new)
        else -> new
    }

    private fun transitionToLoadingState(old: State<UI_MODEL>, new: Loading<UI_MODEL>) = new.copy(
          new.data ?: old.data, // keep showing old data while displaying a loading indicator
          old.data == null // show a full-screen loading indicator if no data is present
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
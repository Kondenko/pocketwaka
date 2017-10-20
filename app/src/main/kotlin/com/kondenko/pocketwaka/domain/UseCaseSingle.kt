package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import timber.log.Timber

/**
 * The base class for all UseCases in the application that use Single.
 */
abstract class UseCaseSingle<PARAMS, RESULT>(private val schedulers: SchedulerContainer) : Disposable {

    private var disposable: Disposable? = null

    abstract fun build(params: PARAMS? = null): Single<RESULT>

    fun execute(params: PARAMS?, onSuccess: (RESULT) -> Unit, onError: (Throwable) -> Unit): Single<RESULT> {
        val single = build(params)
                .doOnEvent { result, error -> Timber.i("Result: $result \n Error: $error") }
                .subscribeOn(schedulers.workerScheduler)
                .observeOn(schedulers.uiScheduler)
        disposable = single.subscribe(onSuccess, onError)
        return single
    }

    fun execute(onSuccess: (RESULT) -> Unit, onError: (Throwable) -> Unit) = execute(null, onSuccess, onError)

    override fun dispose() {
        disposable?.dispose()
    }

    override fun isDisposed() = disposable?.isDisposed ?: true

}
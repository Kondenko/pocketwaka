package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Observable].
 */
abstract class UseCaseObservable<PARAMS, RESULT>(private val schedulers: SchedulersContainer) : UseCase<PARAMS, RESULT, Observable<RESULT>> {

    private var disposable: Disposable? = null

    abstract override fun build(params: PARAMS?): Observable<RESULT>

    override fun invoke(params: PARAMS?, onSuccess: (RESULT) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit): Observable<RESULT> {
        return build(params)
                .subscribeOn(schedulers.workerScheduler)
                .observeOn(schedulers.uiScheduler)
                .doFinally(onFinish)
                .also {
                    disposable = it.subscribe(onSuccess, onError)
                }
    }

    override fun dispose() {
        disposable?.dispose()
    }

    override fun isDisposed() = disposable?.isDisposed ?: true

}
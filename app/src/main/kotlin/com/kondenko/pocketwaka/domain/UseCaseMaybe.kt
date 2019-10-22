package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Maybe].
 */
abstract class UseCaseMaybe<PARAMS, RESULT>(private val schedulers: SchedulersContainer) : UseCase<PARAMS, RESULT, Maybe<RESULT>>, Disposable {

    private var disposable: Disposable? = null

    abstract override fun build(params: PARAMS?): Maybe<RESULT>

    override fun invoke(params: PARAMS?, onSuccess: (RESULT) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit): Maybe<RESULT> {
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
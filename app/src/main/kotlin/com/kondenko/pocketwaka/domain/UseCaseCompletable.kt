package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Completable]
 */
abstract class UseCaseCompletable<PARAMS>(private val schedulers: SchedulerContainer) : CompletableUseCase<PARAMS, Completable>, Disposable {

    private var disposable: Disposable? = null

    override abstract fun build(params: PARAMS?): Completable

    override fun execute(params: PARAMS?, onSuccess: () -> Unit, onError: (Throwable) -> Unit): Completable {
        val single = build(params)
                .subscribeOn(schedulers.workerScheduler)
                .observeOn(schedulers.uiScheduler)
        disposable = single.subscribe(onSuccess, onError)
        return single
    }

    override fun dispose() {
        disposable?.dispose()
    }

    override fun isDisposed() = disposable?.isDisposed ?: true

}
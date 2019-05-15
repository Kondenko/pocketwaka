package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Completable]
 */
abstract class UseCaseCompletable<PARAMS>(private val schedulers: SchedulerContainer) : UseCase<PARAMS,Nothing, Completable>, Disposable {

    private var disposable: Disposable? = null

    abstract override fun build(params: PARAMS?): Completable

    override fun execute(params: PARAMS?, onSuccess: (Nothing) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit, andThen: (() -> Completable)?): Completable {
        val single = build(params)
                .subscribeOn(schedulers.workerScheduler)
                .observeOn(schedulers.uiScheduler)
                .andThen { andThen?.invoke() }
        disposable = single.subscribe(onFinish, onError)
        return single
    }

    override fun dispose() {
        disposable?.dispose()
    }

    override fun isDisposed() = disposable?.isDisposed ?: true

}
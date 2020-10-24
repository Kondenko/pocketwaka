package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Completable
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Completable]
 */
abstract class UseCaseCompletable<PARAMS>(private val schedulers: SchedulersContainer) : DisposableUseCase<PARAMS, Nothing, Completable>, Disposable {

    private var disposable: Disposable? = null

    abstract override fun build(params: PARAMS?): Completable

    override fun invoke(params: PARAMS?, onSuccess: (Nothing) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit): Completable {
        return build(params)
              .subscribeOn(schedulers.workerScheduler)
              .observeOn(schedulers.uiScheduler)
              .also {
                  disposable = it.subscribe(onFinish, onError)
              }
    }

    override fun dispose() {
        disposable?.dispose()
    }

    override fun isDisposed() = disposable?.isDisposed ?: true

}
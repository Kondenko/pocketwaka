package com.kondenko.pocketwaka.domain

import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.Single
import io.reactivex.disposables.Disposable

/**
 * The base class for all UseCases that use [Single].
 */
abstract class UseCaseSingle<PARAMS, RESULT>(private val schedulers: SchedulerContainer) : UseCase<PARAMS, RESULT, Single<RESULT>>, Disposable {

    private var disposable: Disposable? = null

    override abstract fun build(params: PARAMS?): Single<RESULT>

    override fun execute(params: PARAMS?, onSuccess: (RESULT) -> Unit, onError: (Throwable) -> Unit): Single<RESULT> {
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
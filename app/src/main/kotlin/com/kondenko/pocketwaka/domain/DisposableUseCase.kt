package com.kondenko.pocketwaka.domain

import io.reactivex.disposables.Disposable

/**
 * UseCase is an entity that performs some operation and provides callbacks with either a result or an error.
 * It's also responsible for performing the operation in the background thread.
 *
 * @param PARAMS prerequisites for executing this UseCase.
 * @param RESULT the result of the performed operation.
 * @param CONTEXT determines the way we run this operation (like [io.reactivex.Observable], [io.reactivex.Completable] or [io.reactivex.Single])
 */
interface DisposableUseCase<PARAMS, RESULT, CONTEXT> : UseCase<PARAMS, RESULT, CONTEXT>, Disposable {

    fun build(params: PARAMS? = null): CONTEXT

}
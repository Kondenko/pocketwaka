package com.kondenko.pocketwaka.domain

/**
 * A UseCase that returns nothing and only provides a callback for its completion.
 *
 * @see UseCase
 */
 interface CompletableUseCase<PARAMS, CONTEXT> {

    fun build(params: PARAMS? = null): CONTEXT

    fun execute(params: PARAMS? = null, onSuccess: () -> Unit = {}, onError: (Throwable) -> Unit = {}): CONTEXT

}
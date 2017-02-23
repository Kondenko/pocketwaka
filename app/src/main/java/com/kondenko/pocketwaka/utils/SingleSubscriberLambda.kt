package com.kondenko.pocketwaka.utils

import rx.SingleSubscriber

/**
 * A simple wrapper created to reduce boilerplate code
 */
class SingleSubscriberLambda<T>(val success: (T) -> Unit, val error: (Throwable?) -> Unit) : SingleSubscriber<T>() {

    override fun onSuccess(value: T) {
        success(value)
    }

    override fun onError(error: Throwable?) {
        error(error)
    }

}
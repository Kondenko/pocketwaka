package com.kondenko.pocketwaka.screens.base

import android.accounts.NetworkErrorException

sealed class State<out T> {

    data class Loading<T>(val skeletonData: T) : State<T>()

    data class Offline<T>(val data: T?, val lastUpdated: Long? = null) : State<T>()

    object Empty : State<Nothing>()

    data class Success<T>(val data: T) : State<T>()

    sealed class Failure<out T>(
            open val data: T?,
            open val exception: Throwable? = null,
            open val isFatal: Boolean = false
    ) : State<Nothing>() {

        data class NoNetwork<T>(
                override val data: T? = null,
                override val exception: Throwable? = NetworkErrorException("Device is offline"),
                override val isFatal: Boolean = false
        ) : Failure<T>(data, exception, isFatal)

        data class UnknownRange<T>(
                override val data: T? = null,
                override val isFatal: Boolean = false
        ) : Failure<T>(data, IllegalArgumentException("Unknown range"), isFatal)

        data class Unknown<T>(
                override val data: T? = null,
                override val exception: Throwable? = null,
                override val isFatal: Boolean = false
        ) : Failure<T>(data, exception, isFatal)

    }

}

fun <T> State.Failure<T>.copyFrom(data: T?, exception: Throwable? = this.exception): State.Failure<T?> {
    return when (this) {
        is State.Failure.NoNetwork<T> -> this.copy(data, exception)
        is State.Failure.UnknownRange<T> -> this.copy(data)
        is State.Failure.Unknown<T> -> this.copy(data, exception)
    }
}
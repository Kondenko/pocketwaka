package com.kondenko.pocketwaka.screens.base

import android.accounts.NetworkErrorException

sealed class State<out T> {

    data class Success<T>(val data: T) : State<T>()

    sealed class Failure<out T>(open val data: T?, open val exception: Throwable? = null) : State<Nothing>() {

        data class NoNetwork<T>(override val data: T? = null, override val exception: Throwable? = NetworkErrorException("Device is offline"))
            : Failure<T>(data, exception)

        data class UnknownRange<T>(override val data: T? = null)
            : Failure<T>(data, IllegalArgumentException("Unknown range"))

        data class Unknown<T>(override val data: T? = null, override val exception: Throwable? = null)
            : Failure<T>(data, exception)

    }

    data class Loading<T>(val skeletonData: T) : State<T>()

    data class Offline<T>(val data: T?, val lastUpdated: Long? = null) : State<T>()

    object Empty : State<Nothing>()

}

// fun <T> State.Failure<T>.copyFrom(other: State.Success<T>): State.Failure<T?> = this.copyFrom(other.data)

// fun <T> State.Failure<T>.copyFrom(other: State.Failure<T?>): State.Failure<T?> = this.copyFrom(other.data, other.exception)

fun <T> State.Failure<T>.copyFrom(data: T?, exception: Throwable? = this.exception): State.Failure<T?> {
    return when (this) {
        is State.Failure.NoNetwork<T> -> this.copy(data, exception)
        is State.Failure.UnknownRange<T> -> this.copy(data)
        is State.Failure.Unknown<T> -> this.copy(data, exception)
    }
}
package com.kondenko.pocketwaka.screens.base

sealed class State<out T> {
    open class Success<T>(val data: T) : State<T>()
    class Offline<T>(data: T?, val lastUpdated: Long?) : State<T>()
    data class Failure(val errorType: ErrorType) : State<Nothing>()
    data class Loading<T>(val skeletonData: T) : State<T>()
    object Empty : State<Nothing>()
    object Initial : State<Nothing>()
}

sealed class ErrorType(val exception: Throwable? = null, val message: String? = null) {
    object NoNetwork : ErrorType()
    object UnknownRange : ErrorType()
    object Timeout : ErrorType()
    class Unknown(error: Throwable? = null, message: String? = null) : ErrorType(error, message)
}
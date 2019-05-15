package com.kondenko.pocketwaka.screens.base

sealed class State<out T> {
    data class Success<T>(val data: T) : State<T>()
    data class Failure(val error: Throwable? = null, val message: String? = null) : State<Nothing>()
    data class Loading<T>(val skeletonData: T) : State<T>()
    object Empty : State<Nothing>()
}
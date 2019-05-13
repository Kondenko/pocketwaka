package com.kondenko.pocketwaka.screens.base

sealed class State<out T> {
    data class Success<T>(val data: T) : State<T>()
    data class Failure(val error: Throwable? = null, val message: String? = null) : State<Nothing>()
    object Empty : State<Nothing>()
    object Loading : State<Nothing>()
}
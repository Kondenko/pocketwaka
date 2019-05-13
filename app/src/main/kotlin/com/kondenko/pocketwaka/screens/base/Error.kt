package com.kondenko.pocketwaka.screens.base

sealed class Error {
    object NoNetwork
    data class Other(val exception: Throwable)
}
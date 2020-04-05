package com.kondenko.pocketwaka.screens.main

sealed class MainState {
    object GoToContent : MainState()
    data class GoToLogin(val isForcedLogOut: Boolean = false) : MainState()
    data class Error(val cause: Throwable) : MainState()
}
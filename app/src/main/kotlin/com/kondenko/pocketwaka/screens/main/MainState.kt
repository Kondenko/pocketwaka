package com.kondenko.pocketwaka.screens.main

sealed class MainState {
    object ShowData : MainState()
    object GoToLogin : MainState()
    data class Error(val cause: Throwable) : MainState()
}
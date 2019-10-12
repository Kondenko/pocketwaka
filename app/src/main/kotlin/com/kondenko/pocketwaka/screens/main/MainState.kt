package com.kondenko.pocketwaka.screens.main

sealed class MainState {
    object ShowData : MainState()
    object ShowLoginScreen : MainState()
    object LogOut : MainState()
    data class Error(val cause: Throwable) : MainState()
}
package com.kondenko.pocketwaka.screens.main

sealed class MainAction {
    object GoToContent : MainAction()
    data class GoToLogin(val isForcedLogOut: Boolean = false) : MainAction()
    data class OpenWebView(val url: String, val redirectUrl: String) : MainAction()
    object CloseWebView : MainAction()
    data class ShowError(val cause: Throwable) : MainAction()
}
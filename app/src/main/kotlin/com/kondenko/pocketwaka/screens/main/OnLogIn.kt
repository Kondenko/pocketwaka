package com.kondenko.pocketwaka.screens.main

interface OnLogIn {
    fun logIn()
    fun openWebView(url: String, redirectUrl: String)
    fun closeWebView()
}
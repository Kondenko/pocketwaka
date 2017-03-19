package com.kondenko.pocketwaka.screens.activities.main

import com.kondenko.pocketwaka.api.oauth.AccessToken

interface MainActivityView {
    fun onTokenRefreshSuccess(refreshToken: AccessToken)
    fun onTokenRefreshFail(error: Throwable?)
}
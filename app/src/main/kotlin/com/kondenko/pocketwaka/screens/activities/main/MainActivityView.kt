package com.kondenko.pocketwaka.screens.activities.main

import com.kondenko.pocketwaka.BaseView
import com.kondenko.pocketwaka.api.oauth.AccessToken

interface MainActivityView : BaseView {
    fun onTokenRefreshSuccess(refreshToken: AccessToken)
    fun onTokenRefreshFail(error: Throwable?)
}
package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.BaseView

interface MainView : BaseView {
    fun onTokenRefreshSuccess(refreshToken: AccessToken)
    fun onTokenRefreshFail(error: Throwable?)
}
package com.kondenko.pocketwaka.screens.login

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.api.oauth.AccessToken

interface LoginView {

    fun onGetTokenSuccess(token: AccessToken)

    fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int)

}
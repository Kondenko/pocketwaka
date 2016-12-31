package com.kondenko.pocketwaka.screens.login

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.api.oauth.AccessToken

interface LoginView {

    fun displayToken(token: AccessToken)

    fun displayError(e: Throwable?, @StringRes messageStringRes: Int)

}
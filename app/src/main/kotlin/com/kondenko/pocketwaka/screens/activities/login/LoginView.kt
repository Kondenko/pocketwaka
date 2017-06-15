package com.kondenko.pocketwaka.screens.activities.login

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.BaseView
import com.kondenko.pocketwaka.api.oauth.AccessToken

interface LoginView : BaseView {

    fun onGetTokenSuccess(token: AccessToken)

    fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int)

}
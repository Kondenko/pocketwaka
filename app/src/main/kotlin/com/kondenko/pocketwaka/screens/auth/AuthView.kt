package com.kondenko.pocketwaka.screens.auth

import android.support.annotation.StringRes
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.BaseView

interface AuthView : BaseView {

    fun onGetTokenSuccess(token: AccessToken)

    fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int)

}
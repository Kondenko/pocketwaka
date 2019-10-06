package com.kondenko.pocketwaka.screens.login

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.screens.base.BaseView

interface LoginView : BaseView {

    fun openAuthUrl(url: String)

    fun onGetTokenSuccess(token: AccessToken)

    fun showLoading()

}
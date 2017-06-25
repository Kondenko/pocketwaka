package com.kondenko.pocketwaka.screens.activities.main

import android.content.Context
import com.kondenko.pocketwaka.BasePresenter
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.KeysManager
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.api.services.LoginService
import com.kondenko.pocketwaka.utils.Utils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainActivityPresenter @Inject constructor(val loginService: LoginService, val view: MainView) : BasePresenter() {

    private val TAG = this.javaClass.simpleName

    var subscription: Disposable? = null

    fun onCreate(context: Context) {
        val token = AccessTokenUtils.getTokenObject(context)
        val id = KeysManager.getAppId()
        val secret = KeysManager.getAppSecret()
        val redirectUri = Const.AUTH_REDIRECT_URI
        updateToken(token, id, secret, redirectUri)
    }

    fun onStop() {
        Utils.unsubscribe(subscription)
    }

    fun updateToken(accessToken: AccessToken, id: String, secret: String, redirectUri: String) {
        if (!accessToken.isValid()) {
            subscription = loginService.getRefreshToken(id, secret, redirectUri, accessToken.token_type, accessToken.refresh_token)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { refreshToken -> view.onTokenRefreshSuccess(refreshToken) },
                            { error -> view.onTokenRefreshFail(error) }
                    )
        }
    }


}
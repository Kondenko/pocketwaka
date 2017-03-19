package com.kondenko.pocketwaka.screens.activities.main

import android.content.Context
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.KeysManager
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.api.services.TokenService
import com.kondenko.pocketwaka.utils.SingleSubscriberLambda
import com.kondenko.pocketwaka.utils.Utils
import rx.SingleSubscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class MainActivityPresenter(val view: MainActivityView) {

    private val TAG = this.javaClass.simpleName

    @Inject
    lateinit var tokenService: TokenService

    var subscription: Subscription? = null
    val subscriber: SingleSubscriber<AccessToken>

    init {
        App.serviceComponent.inject(this)
        subscriber = SingleSubscriberLambda<AccessToken>(
                { refreshToken -> view.onTokenRefreshSuccess(refreshToken) },
                { error -> view.onTokenRefreshFail(error) }
        )
    }

    fun onCreate(context: Context) {
        val token = AccessTokenUtils.getTokenObject(context)
        updateToken(tokenService, token)
    }

    fun onStop() {
        Utils.unsubscribe(subscription)
    }

    fun updateToken(service: TokenService, token: AccessToken) {
        if (!token.isValid()) {
            val id = KeysManager.getAppId()
            val secret = KeysManager.getAppSecret()
            val redirectUri = Const.AUTH_REDIRECT_URI
            subscription = service.getRefreshToken(id, secret, redirectUri, token.token_type, token.refresh_token)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber)
        }
    }

}
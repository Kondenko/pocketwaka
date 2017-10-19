package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.screens.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@PerApp
class MainActivityPresenter @Inject constructor(val authService: AuthService) : BasePresenter<MainView>() {

    var disposable: Disposable? = null

    fun onCreate() {
//        val token = AccessTokenRepository.getTokenObject(context)
//        val id = EncryptedKeysRepository.getAppId()
//        val secret = EncryptedKeysRepository.getAppSecret()
//        val redirectUri = Const.AUTH_REDIRECT_URI
//        updateToken(token, id, secret, redirectUri)
    }

    fun onStop() {
        disposable?.dispose()
    }

    fun updateToken(accessToken: AccessToken, id: String, secret: String, redirectUri: String) {
        if (!accessToken.isValid()) {
            disposable = authService.getRefreshToken(id, secret, redirectUri, accessToken.token_type, accessToken.refresh_token)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { refreshToken -> view?.onTokenRefreshSuccess(refreshToken) },
                            { error -> view?.onError(error) }
                    )
        }
    }


}
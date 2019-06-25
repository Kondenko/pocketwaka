package com.kondenko.pocketwaka.screens.auth

import com.kondenko.pocketwaka.domain.auth.GetAccessToken
import com.kondenko.pocketwaka.domain.auth.GetAuthUrl
import com.kondenko.pocketwaka.screens.base.BasePresenter


class AuthPresenter(private val getAuthUrl: GetAuthUrl, private val getAccessToken: GetAccessToken) : BasePresenter<AuthView>() {

    fun onLoginButtonClicked() {
        getAuthUrl.invoke(
                onSuccess = { url -> view?.openAuthUrl(url) },
                onError = { view?.showError(it) }
        )
    }

    fun getToken(code: String) {
        view?.showLoading()
        getAccessToken.invoke(
                params = code,
                onSuccess = { token -> view?.onGetTokenSuccess(token) },
                onError = { view?.showError(it) }
        )
    }

    override fun detach() {
        dispose(getAccessToken, getAuthUrl)
    }

}
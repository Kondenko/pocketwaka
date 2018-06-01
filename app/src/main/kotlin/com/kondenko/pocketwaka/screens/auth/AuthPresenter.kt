package com.kondenko.pocketwaka.screens.auth

import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.auth.GetAccessToken
import com.kondenko.pocketwaka.domain.auth.GetAuthUrl
import com.kondenko.pocketwaka.screens.base.BasePresenter
import javax.inject.Inject

@PerApp
class AuthPresenter
@Inject constructor(private val getAuthUrl: GetAuthUrl, private val getAccessToken: GetAccessToken)
    : BasePresenter<AuthView>() {

    fun onLoginButtonClicked() {
        getAuthUrl.execute(
                onSuccess = { url -> view?.openAuthUrl(url) },
                onError = { view?.showError(it) }
        )
    }

    fun getToken(code: String) {
        getAccessToken.execute(
                params = code,
                onSuccess = { token -> view?.onGetTokenSuccess(token) },
                onError = { view?.showError(it, R.string.error_logging_in) }
        )
    }

    override fun detach() {
        dispose(getAccessToken, getAuthUrl)
    }

}
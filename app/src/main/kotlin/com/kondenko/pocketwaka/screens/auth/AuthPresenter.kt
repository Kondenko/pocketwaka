package com.kondenko.pocketwaka.screens.auth

import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.domain.auth.GetAccessToken
import com.kondenko.pocketwaka.domain.auth.GetAuthUrl
import com.kondenko.pocketwaka.screens.BasePresenter
import timber.log.Timber
import javax.inject.Inject

@PerApp
class AuthPresenter
@Inject constructor(private val getAccessToken: GetAccessToken, private val getAuthUrl: GetAuthUrl)
    : BasePresenter<AuthView>() {

    fun onLoginButtonClicked() {
        getAuthUrl.execute(null,
                { url ->
                    view?.openAuthUrl(url)
                },
                { error ->
                    view?.onError(error)
                }
        )
    }

    fun getToken(code: String) {
        getAccessToken.execute(code,
                { token -> view?.onGetTokenSuccess(token) },
                { error -> view?.onError(error, R.string.error_logging_in)})
    }

    override fun detach() {
        dispose(getAccessToken, getAuthUrl)
    }

}
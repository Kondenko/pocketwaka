package com.kondenko.pocketwaka.screens.login

import android.net.Uri
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.domain.auth.GetAccessToken
import com.kondenko.pocketwaka.domain.auth.GetAuthUrl
import com.kondenko.pocketwaka.screens.base.BasePresenter


class LoginPresenter(private val getAuthUrl: GetAuthUrl, private val getAccessToken: GetAccessToken) : BasePresenter<LoginView>() {

    private var isLoggingIn = false

    fun onLoginButtonClicked(forceWebView: Boolean = false) {
        isLoggingIn = true
        getAuthUrl.invoke(
              onSuccess = { url -> view?.openAuthUrl(url, Const.AUTH_REDIRECT_URI, forceWebView) },
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

    fun checkIfAuthIsSuccessful(uri: Uri?) {
        if (isLoggingIn) {
            isLoggingIn = if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
                val code = uri.getQueryParameter("code")
                if (code != null) {
                    getToken(code)
                } else uri.getQueryParameter("error")?.let {
                    view?.showError(RuntimeException(it))
                }
                false
            } else {
                view?.onLoginCancelled()
                false
            }
        }
    }

    override fun detach() {
        dispose(getAccessToken, getAuthUrl)
    }

}
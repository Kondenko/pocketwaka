package com.kondenko.pocketwaka.screens.activities.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.KeysManager
import com.kondenko.pocketwaka.api.services.TokenService
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.Utils
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenter(val view: LoginView) {

    @Inject
    lateinit var service: TokenService

    var subscription: Subscription? = null

    private val appId by lazy {
        KeysManager.getAppId()
    }

    private val appSecret by lazy {
        KeysManager.getAppSecret()
    }

    init {
        App.serviceComponent.inject(this)
    }

    fun onResume(intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                getToken(appId, appSecret, Const.AUTH_REDIRECT_URI, Const.GRANT_TYPE_AUTH_CODE, code)
            } else if (uri.getQueryParameter("error") != null) {
                view.onGetTokenError(null, R.string.error_logging_in)
            }
        }
    }

    fun onStop() {
        Utils.unsubscribe(subscription)
    }

    fun onAuthPageOpen(context: Context) {
        val scopes = arrayOf(Const.SCOPE_EMAIL, Const.SCOPE_READ_LOGGED_TIME, Const.SCOPE_READ_STATS, Const.SCOPE_READ_TEAMS)
        val uri = getAuthUrl(Const.RESPONSE_TYPE_CODE, scopes)
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(R.color.color_primary)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(uri))
    }

    private fun getAuthUrl(responseType: String, scope: Array<String>): String {
        return (Const.URL_AUTH +
                "?client_id=$appId" +
                "&response_type=$responseType" +
                "&redirect_uri=${Const.AUTH_REDIRECT_URI}" +
                "&scope=${scope.joinToString(",")}")
    }

    fun getToken(id: String, secret: String, redirectUri: String, grantType: String, code: String) {
        subscription = service.getAccessToken(id, secret, redirectUri, grantType, code)
                .subscribeOn(Schedulers.newThread())
                .doOnSuccess { token -> token.created_at = Utils.currentTimeSec() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { token -> view.onGetTokenSuccess(token) },
                        { error -> view.onGetTokenError(error, R.string.error_logging_in) }
                )
    }

}
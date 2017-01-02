package com.kondenko.pocketwaka.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.util.Base64
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.KeysManager
import com.kondenko.pocketwaka.api.oauth.LoginService
import retrofit2.Retrofit
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenterImpl(val view: LoginView) : LoginPresenter {

    @Inject
    lateinit var retrofit: Retrofit

    private val service: LoginService

    private val appId by lazy {
        String(Base64.decode(KeysManager.getAppId(), Base64.DEFAULT))
    }

    private val appSecret by lazy {
        String(Base64.decode(KeysManager.getAppSecret(), Base64.DEFAULT))
    }

    init {
        App.netComponent.inject(this)
        service = retrofit.create(LoginService::class.java)
    }

    override fun onResume(activity: Activity, intent: Intent) {
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                getToken(code)
            } else if (uri.getQueryParameter("error") != null) {
                view.onGetTokenError(null, R.string.error_getting_token)
            }
        }
    }

    override fun onAuthPageOpen(context: Context) {
        val uri = getAuthUrl(Const.RESPONSE_TYPE_CODE, arrayOf(Const.SCOPE_EMAIL))
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(R.color.colorPrimary)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(uri))
    }

    private fun getAuthUrl(responseType: String, scope: Array<String>): String {
        return (Const.AUTH_URL +
                "?client_id=$appId" +
                "&response_type=$responseType" +
                "&redirect_uri=${Const.AUTH_REDIRECT_URI}" +
                "&scope=${scope.joinToString(",")}")
    }

    fun getToken(code: String) {
        service.getAccessToken(appId, appSecret, Const.AUTH_REDIRECT_URI, Const.GRANT_TYPE_AUTH_CODE, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { token ->
                            view.onGetTokenSuccess(token)
                        },
                        { error ->
                            view.onGetTokenError(error, R.string.error_getting_token)
                        }
                )
    }

}
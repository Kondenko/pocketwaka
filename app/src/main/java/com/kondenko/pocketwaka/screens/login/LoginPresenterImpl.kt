package com.kondenko.pocketwaka.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.ApiScope
import com.kondenko.pocketwaka.api.oauth.LoginService
import retrofit2.Retrofit
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

class LoginPresenterImpl(val view: LoginView) : LoginPresenter {
    @Inject
    lateinit var retrofit: Retrofit

    private val service: LoginService


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
                view.displayError(null, R.string.error_getting_token)
            }
        }
    }

    override fun onAuthPageOpen(context: Context) {
        val uri = getAuthUrl(Const.RESPONSE_TYPE_CODE, arrayOf(ApiScope.EMAIL.s))
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(R.color.colorPrimary)
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(uri))
    }

    private fun getAuthUrl(responseType: String, scope: Array<String>): String {
        return (Const.AUTH_URL +
                "?client_id=${Const.APP_ID}" +
                "&response_type=$responseType" +
                "&redirect_uri=${Const.AUTH_REDIRECT_URI}" +
                "&scope=${scope.joinToString(",")}")
    }


    fun getToken(code: String) {
        service.getAccessToken(Const.APP_ID, Const.APP_SECRET, Const.AUTH_REDIRECT_URI, Const.GRANT_TYPE_AUTH_CODE, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { token ->
                            view.displayToken(token)
                        },
                        { error ->
                            view.displayError(error, R.string.error_getting_token)
                        }
                )
    }

}
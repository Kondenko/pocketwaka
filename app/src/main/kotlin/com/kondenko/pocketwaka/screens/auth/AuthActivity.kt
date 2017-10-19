package com.kondenko.pocketwaka.screens.auth

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.main.MainActivity
import timber.log.Timber
import javax.inject.Inject


class AuthActivity : AppCompatActivity(), AuthView {
    @Inject
    lateinit var presenter: AuthPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.authComponent.inject(this)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.button_login).setOnClickListener { presenter.onLoginButtonClicked() }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
        presenter.attach(this)
    }

    override fun onResume() {
        super.onResume()
        Timber.i("onResume")
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                presenter.getToken(code)
            } else if (uri.getQueryParameter("error") != null) {
                onError(null, R.string.error_logging_in)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    override fun openAuthUrl(url: String) {
        Timber.i("openAuthUrl: $url")
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.color_primary))
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onError(throwable: Throwable?, @StringRes messageStringRes: Int?) {
        throwable?.printStackTrace()
        messageStringRes?.let { Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show() }
    }

}

package com.kondenko.pocketwaka.screens.auth

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding2.view.RxView
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.main.MainActivity
import com.kondenko.pocketwaka.utils.report
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject


class AuthActivity : AppCompatActivity(), AuthView {

    private val presenter: AuthPresenter by inject()

    private var connection: CustomTabsServiceConnection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        RxView.clicks(button_login).subscribe { presenter.onLoginButtonClicked() }
        if (BuildConfig.DEBUG) {
            val clicksRequired = 3
            RxView.longClicks(button_login)
                    .buffer(clicksRequired)
                    .subscribe { Crashlytics.getInstance().crash() }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                presenter.getToken(code)
            } else uri.getQueryParameter("error")?.let {
                showError(RuntimeException(it), R.string.error_logging_in)
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
        connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(componentName: ComponentName, client: CustomTabsClient) {
                client.warmup(0L) // This prevents backgrounding after redirection
                val customTabsIntent = with(CustomTabsIntent.Builder()) {
                    setToolbarColor(ContextCompat.getColor(this@AuthActivity, R.color.color_primary))
                    build()
                }
                customTabsIntent.launchUrl(this@AuthActivity, Uri.parse(url))
            }

            override fun onServiceDisconnected(name: ComponentName) {

            }
        }
        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", connection)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun showError(throwable: Throwable?, @StringRes messageStringRes: Int?) {
        throwable?.report()
        messageStringRes?.let { Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        connection?.let {
            this.unbindService(it)
            connection = null
        }
        super.onDestroy()
    }
}

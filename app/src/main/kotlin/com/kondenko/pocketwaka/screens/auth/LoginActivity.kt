package com.kondenko.pocketwaka.screens.auth

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding2.view.RxView
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.main.MainActivity
import com.kondenko.pocketwaka.ui.ButtonStateWrapper
import com.kondenko.pocketwaka.utils.report
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject


class LoginActivity : AppCompatActivity(), LoginView {

    private val presenter: LoginPresenter by inject()

    private var connection: CustomTabsServiceConnection? = null

    private lateinit var loadingButtonStateWrapper: ButtonStateWrapper

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loadingView.z = 100f
        loadingButtonStateWrapper = ButtonStateWrapper(
                buttonLogin,
                loadingView,
                getString(R.string.loginactivity_subtitle_error_action)
        )
        loadingButtonStateWrapper.setDefault()
        RxView.clicks(buttonLogin)
                .filter {
                    // setClickable(false) is reset to true after setting a click listener
                    buttonLogin.isClickable
                }
                .subscribe { presenter.onLoginButtonClicked() }
        if (BuildConfig.DEBUG) {
            val clicksRequired = 3
            RxView.longClicks(buttonLogin)
                    .buffer(clicksRequired)
                    .subscribe { Crashlytics.getInstance().crash() }
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onResume() {
        super.onResume()
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                presenter.getToken(code)
            } else uri.getQueryParameter("error")?.let {
                showError(RuntimeException(it))
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
                    setToolbarColor(ContextCompat.getColor(this@LoginActivity, android.R.color.white))
                    build()
                }
                customTabsIntent.launchUrl(this@LoginActivity, Uri.parse(url))
            }

            override fun onServiceDisconnected(name: ComponentName) {
            }
        }
        getChromePackage()?.let {
            CustomTabsClient.bindCustomTabsService(this, getChromePackage(), connection)
        } ?: showError(null, R.string.loginactivity_error_no_browser)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun showLoading() {
        loadingButtonStateWrapper.setLoading()
        textViewSubhead.apply {
            setText(R.string.loginactivity_subtitle_loading)
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_App_Login_Subhead)
        }
    }

    override fun showError(throwable: Throwable?, @StringRes messageStringRes: Int?) {
        throwable?.report()
        loadingButtonStateWrapper.setError()
        textViewSubhead.apply {
            setText(messageStringRes ?: R.string.loginactivity_error_generic)
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_App_Login_Subhead_Error)
        }
    }

    private fun getChromePackage(): String? {
        fun Iterable<PackageInfo>.find(packageName: String): String? {
            return find { it.packageName == packageName }?.packageName
        }

        val chrome = "com.chrome"
        val stable = "com.android.chrome"
        val beta = "$chrome.beta"
        val dev = "$chrome.dev"
        val canary = "$chrome.canary"
        val apps = packageManager.getInstalledPackages(0)
        return apps.run {
            find(stable) ?: find(beta) ?: find(dev) ?: find(canary)
        }
    }

    override fun onDestroy() {
        connection?.let {
            this.unbindService(it)
            connection = null
        }
        super.onDestroy()
    }

}

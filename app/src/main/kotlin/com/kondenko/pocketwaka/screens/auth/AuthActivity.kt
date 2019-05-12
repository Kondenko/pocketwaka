package com.kondenko.pocketwaka.screens.auth

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.core.widget.TextViewCompat
import com.crashlytics.android.Crashlytics
import com.jakewharton.rxbinding2.view.RxView
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.screens.main.MainActivity
import com.kondenko.pocketwaka.ui.ButtonStateWrapper
import com.kondenko.pocketwaka.ui.LoadingView
import com.kondenko.pocketwaka.utils.report
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import kotlin.math.roundToInt


class AuthActivity : AppCompatActivity(), AuthView {

    private val presenter: AuthPresenter by inject()

    private var connection: CustomTabsServiceConnection? = null

    private lateinit var loadingButtonStateWrapper: ButtonStateWrapper

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        getDrawable(R.drawable.loading_dot)?.let { dot ->
            val loadingViewPadding = resources.getDimension(R.dimen.padding_login_loading_view_side).roundToInt()
            val loadingView = LoadingView(this).apply {
                dotsNumber = 3
                dotDrawable = dot
                dotMargin = resources.getDimension(R.dimen.margin_login_loading_view_dot).roundToInt()
                updatePadding(left = loadingViewPadding, right = loadingViewPadding)
            }
            loadingButtonStateWrapper = ButtonStateWrapper.wrap(
                    buttonLogin,
                    loadingView,
                    getString(R.string.loginactivity_subtitle_error_action)
            )
            loadingButtonStateWrapper.setDefault()
        }
        RxView.clicks(buttonLogin)
                .filter { buttonLogin.isClickable } // for some reason setClickable(false) isn't enough
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
                    setToolbarColor(ContextCompat.getColor(this@AuthActivity, android.R.color.white))
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
            setText(R.string.loginactivity_subtitle_error)
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_App_Login_Subhead_Error)
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

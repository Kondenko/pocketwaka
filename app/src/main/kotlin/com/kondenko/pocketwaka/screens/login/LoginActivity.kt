package com.kondenko.pocketwaka.screens.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.analytics.ScreenTracker
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.screens.main.MainActivity
import com.kondenko.pocketwaka.ui.ButtonStateWrapper
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.extensions.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.report
import kotlinx.android.synthetic.main.activity_login.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

// TODO Migrate to ViewModel
class LoginActivity : AppCompatActivity(), LoginView {

    private val screenTracker: ScreenTracker by inject()

    private val eventTracker: EventTracker by inject()

    private val presenter: LoginPresenter by inject()

    private val browserWindow: BrowserWindow by inject { parametersOf(this as Context, this as LifecycleOwner) }

    private lateinit var loadingButtonStateWrapper: ButtonStateWrapper

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
        buttonLogin.clicks()
              .filter {
                  // setClickable(false) is reset to true after setting a click listener
                  buttonLogin.isClickable
              }
              .doOnEach { eventTracker.log(Event.Login.ButtonClicked) }
              .subscribe { presenter.onLoginButtonClicked() }
              .attachToLifecycle(this)
        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEBUG) {
            val clicksRequired = 3
            buttonLogin.longClicks()
                  .buffer(clicksRequired)
                  .subscribe { throw RuntimeException("Test exception") }
                  .attachToLifecycle(this)
        }
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onResume() {
        super.onResume()
        screenTracker.log(this, Screen.Auth)
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(Const.AUTH_REDIRECT_URI)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                presenter.getToken(code)
            } else uri.getQueryParameter("error")?.let {
                showError(RuntimeException(it))
            }
        } else {
            eventTracker.log(Event.Login.Canceled)
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
        browserWindow.openUrl(url)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        eventTracker.log(Event.Login.Successful)
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
        eventTracker.log(Event.Login.Unsuccessful)
        throwable?.report()
        loadingButtonStateWrapper.setError()
        textViewSubhead.apply {
            setText(messageStringRes ?: R.string.loginactivity_error_generic)
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_App_Login_Subhead_Error)
        }
    }

}

package com.kondenko.pocketwaka.screens.login

import android.os.Bundle
import android.view.*
import androidx.annotation.StringRes
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.view.longClicks
import com.kondenko.pocketwaka.BuildConfig
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.analytics.ScreenTracker
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.screens.main.OnLogIn
import com.kondenko.pocketwaka.ui.ButtonStateWrapper
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.extensions.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.report
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit

// TODO Migrate to ViewModel
class FragmentLogin : Fragment(), LoginView {

    companion object {
        const val wasLogoutForced = "logoutForced"
    }

    private val presenter: LoginPresenter by inject()

    private val onLogin: OnLogIn by sharedViewModel<MainViewModel>()

    private val screenTracker: ScreenTracker by inject()

    private val eventTracker: EventTracker by inject()

    private val browserWindow: BrowserWindow by inject { parametersOf(context, this as LifecycleOwner) }

    private lateinit var loadingButtonStateWrapper: ButtonStateWrapper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater
                .cloneInContext(ContextThemeWrapper(activity, R.style.LoginTheme))
                .inflate(R.layout.fragment_login, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (arguments?.getBoolean(wasLogoutForced, false) == true) {
            textViewSubhead.setText(R.string.all_error_invalid_access)
            TextViewCompat.setTextAppearance(textViewSubhead, R.style.TextAppearance_App_Login_Subhead_Error)
        }
    }

    override fun onResume() {
        super.onResume()
        screenTracker.log(activity, Screen.Auth)
        onLoginScreenRevisited()
    }

    fun onLoginScreenRevisited() {
        presenter.checkIfAuthIsSuccessful(activity?.intent?.data)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    override fun openAuthUrl(url: String, authRedirectUri: String) {
        browserWindow.openUrl(url)
        browserWindow.onWebViewRequired {
            onLogin.openWebView(url, authRedirectUri)
        }
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        eventTracker.log(Event.Login.Successful)
        onLogin.logIn()
    }

    override fun showLoading() {
        loadingButtonStateWrapper.setLoading()
        textViewSubhead.apply {
            setText(R.string.loginactivity_subtitle_loading)
            TextViewCompat.setTextAppearance(this, R.style.TextAppearance_App_Login_Subhead)
        }
    }

    override fun onLoginCancelled() {
        eventTracker.log(Event.Login.Canceled)
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

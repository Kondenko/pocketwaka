package com.kondenko.pocketwaka

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kondenko.pocketwaka.screens.login.FragmentLogin
import com.kondenko.pocketwaka.screens.main.FragmentContent
import com.kondenko.pocketwaka.screens.main.MainAction
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.transaction
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val webViewBackStack = "webViewBackStack"

    private val vm: MainViewModel by viewModel()

    private val fragmentContent = FragmentContent()

    private val fragmentLogin by lazy {
        FragmentLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.actions().observe(this) {
            when (it) {
                is MainAction.GoToContent -> goToContent()
                is MainAction.GoToLogin -> goToLogin(it.isForcedLogOut)
                is MainAction.ShowError -> showError(it.cause)
                is MainAction.OpenWebView -> showAuthWebView(it.url, it.redirectUrl)
                is MainAction.CloseWebView -> closeWebView()
            }
        }
    }

    private fun goToContent() {
        showFragment(fragmentContent)
    }

    private fun goToLogin(isForcedLogout: Boolean) {
        showFragment(fragmentLogin.apply {
            arguments = bundleOf(FragmentLogin.wasLogoutForced to isForcedLogout)
        })
    }

    private fun showError(throwable: Throwable?) {
        throwable?.report()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    private fun showAuthWebView(url: String, redirectUrl: String) = supportFragmentManager.transaction {
        add(R.id.mainFragmentContainer, FragmentOauthWebView.openUrl(url, redirectUrl))
        addToBackStack(webViewBackStack)
    }

    private fun closeWebView() =
          supportFragmentManager.apply {
              popBackStackImmediate(webViewBackStack, FragmentManager.POP_BACK_STACK_INCLUSIVE)
              executePendingTransactions()
              fragmentLogin.onLoginScreenRevisited()
          }

    private fun showFragment(fragment: Fragment) = supportFragmentManager.transaction {
        replace(R.id.mainFragmentContainer, fragment)
    }

}

package com.kondenko.pocketwaka

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.screens.login.FragmentLogin
import com.kondenko.pocketwaka.screens.main.FragmentContent
import com.kondenko.pocketwaka.screens.main.MainState
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.transaction
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    private val fragmentContent = FragmentContent()

    private val fragmentLogin by lazy {
        FragmentLogin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        vm.state().observe(this) {
            when (it) {
                is MainState.GoToContent -> goToContent()
                is MainState.GoToLogin -> goToLogin(it.isForcedLogOut)
                is MainState.Error -> showError(it.cause)
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

    private fun showFragment(fragment: Fragment) = supportFragmentManager.transaction {
        replace(R.id.mainFragmentContainer, fragment)
    }

}

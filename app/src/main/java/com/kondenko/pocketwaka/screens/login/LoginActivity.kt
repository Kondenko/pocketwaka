package com.kondenko.pocketwaka.screens.login

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.databinding.ActivityLoginBinding
import com.kondenko.pocketwaka.screens.main.MainActivity


class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var loginPresenter: LoginPresenterImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        loginPresenter = LoginPresenterImpl(this)
        binding.buttonOpenWebsite.setOnClickListener {
            loginPresenter.onAuthPageOpen(this)
        }
    }

    override fun onResume() {
        super.onResume()
        loginPresenter.onResume(this, intent)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        AccessTokenUtils.storeToPreferences(token, this)
        this.finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int) {
        error?.printStackTrace()
        Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show()
    }

}

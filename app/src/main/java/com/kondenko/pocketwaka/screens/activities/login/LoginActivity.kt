package com.kondenko.pocketwaka.screens.activities.login

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.screens.activities.main.MainActivity


class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginPresenter = LoginPresenter(this)
        val buttonLogin = findViewById(R.id.button_login)
        buttonLogin.setOnClickListener {
            loginPresenter.onAuthPageOpen(this)
        }
    }

    override fun onResume() {
        super.onResume()
        loginPresenter.onResume(intent)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        AccessTokenUtils.storeToPreferences(token, this)
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    override fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int) {
        error?.printStackTrace()
        Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show()
    }

}

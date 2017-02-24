package com.kondenko.pocketwaka.screens.activities.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onResume() {
        super.onResume()
        loginPresenter.onResume(intent)
    }

    override fun onGetTokenSuccess(token: AccessToken) {
        AccessTokenUtils.saveToken(token, this)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }

    override fun onGetTokenError(error: Throwable?, @StringRes messageStringRes: Int) {
        error?.printStackTrace()
        Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show()
    }

}

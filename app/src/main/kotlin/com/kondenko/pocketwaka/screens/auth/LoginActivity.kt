package com.kondenko.pocketwaka.screens.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenUtils
import com.kondenko.pocketwaka.screens.main.MainActivity
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginView {

    @Inject
    public lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val buttonLogin = findViewById(R.id.button_login)
        buttonLogin.setOnClickListener {
            presenter.onAuthPageOpen(this)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume(intent)
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
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

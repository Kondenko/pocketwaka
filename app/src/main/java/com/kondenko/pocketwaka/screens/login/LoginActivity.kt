package com.kondenko.pocketwaka.screens.login

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var loginPresenter: LoginPresenterImpl

    private lateinit var editTextApi: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login)
        editTextApi = binding.apiKey

        binding.buttonOpenWebsite.setOnClickListener {
            loginPresenter.onAuthPageOpen(this)
        }

        loginPresenter = LoginPresenterImpl(this)
    }

    override fun onResume() {
        super.onResume()
        loginPresenter.onResume(this, intent)
    }

    override fun displayToken(token: AccessToken) {
        i("TOKEN ACQUIRED - $token")
    }

    override fun displayError(e: Throwable?, @StringRes messageStringRes: Int) {
        e(e)
        Toast.makeText(this, getString(messageStringRes), Toast.LENGTH_SHORT).show()
    }

    private fun i(text: String) {
        Log.i(this.javaClass.simpleName, text)
    }

    private fun e(t: Throwable?) {
        if (t != null) Log.e(this.javaClass.simpleName, "${t.message} + \n + ${t.cause}", t)
    }

}

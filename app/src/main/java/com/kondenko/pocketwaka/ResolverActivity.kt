package com.kondenko.pocketwaka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.screens.main.MainActivity

class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var intent = (MainActivity::class.java) if (AccessTokenUtils.isTokenSaved(this)) else LoginActivity::class.java
    }

}

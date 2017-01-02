package com.kondenko.pocketwaka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.screens.main.MainActivity

/**
 * Checks if the user is logged in and proceeds to the appropriate screen
 */
class ResolverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var activityClass = if (AccessTokenUtils.isTokenSaved(this)) MainActivity::class.java else LoginActivity::class.java
        startActivity(Intent(this, activityClass))
    }

}

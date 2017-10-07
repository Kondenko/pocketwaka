package com.kondenko.pocketwaka.screens

import android.app.Activity
import android.content.Intent
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenUtils
import com.kondenko.pocketwaka.screens.auth.AuthActivity
import com.kondenko.pocketwaka.screens.main.MainActivity

/**
 * Checks if the user is logged in and proceeds to the appropriate screen
 */
class ResolverActivity : Activity() {
    override fun onResume() {
        super.onResume()
        val activityClass = if (AccessTokenUtils.isTokenSaved(this)) MainActivity::class.java else AuthActivity::class.java
        this.finish()
        startActivity(Intent(this, activityClass))
    }
}

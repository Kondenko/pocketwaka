package com.kondenko.pocketwaka.screens.activities.main

import android.content.Intent
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.screens.activities.login.LoginActivity

class MainActivityPresenter(val view: MainActivityView) {

    fun logout(activity: MainActivity) {
        activity.finish()
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        AccessTokenUtils.deleteToken(activity)
    }

}
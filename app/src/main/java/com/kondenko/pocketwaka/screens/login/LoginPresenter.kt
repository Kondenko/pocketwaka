package com.kondenko.pocketwaka.screens.login

import android.app.Activity
import android.content.Context
import android.content.Intent

interface LoginPresenter {

    fun onResume(activity: Activity, intent: Intent)

    fun onAuthPageOpen(context: Context)

}
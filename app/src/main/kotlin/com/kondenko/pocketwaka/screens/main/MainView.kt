package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.screens.base.BaseView

interface MainView : BaseView {

    fun showLoginScreen()

    fun showStats()

    fun onLogout()

}
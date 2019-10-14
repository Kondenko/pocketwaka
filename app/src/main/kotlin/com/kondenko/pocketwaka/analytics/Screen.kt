package com.kondenko.pocketwaka.analytics

import com.kondenko.pocketwaka.utils.extensions.className

sealed class Screen {

    data class Auth(val isCompleted: Boolean, val isSuccessful: Boolean) : Screen()

    object Summary : Screen() {
        override fun toString() = className()
    }

    sealed class Stats : Screen() {

        object TabContainer : Stats() {
            override fun toString() = className()
        }

        data class Tab(val range: String?) : Stats()

    }

    object Menu : Screen() {
        override fun toString() = className()
    }

}
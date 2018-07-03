package com.kondenko.pocketwaka.di

import org.koin.log.Logger
import timber.log.Timber

class TimberLogger : Logger {

    private val tag = "Koin"

    override fun debug(msg: String) {
        Timber.tag(tag).d(msg)
    }

    override fun err(msg: String) {
        Timber.tag(tag).e(msg)
    }

    override fun log(msg: String) {
        Timber.tag(tag).i(msg)
    }

}
package com.kondenko.pocketwaka.di

import org.koin.core.logger.Level
import org.koin.core.logger.Logger
import org.koin.core.logger.MESSAGE
import timber.log.Timber

class TimberLogger : Logger() {

    private val tag = "Koin"

    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> Timber.tag(tag).d(msg)
            Level.INFO -> Timber.tag(tag).i(msg)
            Level.ERROR -> Timber.tag(tag).e(msg)
        }
    }

}
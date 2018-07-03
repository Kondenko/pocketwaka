package com.kondenko.pocketwaka.utils

import java.util.concurrent.TimeUnit

class TimeProvider {

    fun getCurrentTimeMillis() = System.currentTimeMillis()

    fun getCurrentTimeSec() = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()).toFloat()

}
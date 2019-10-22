package com.kondenko.pocketwaka.utils

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class ResponseLogger : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(chain.request()).also { Timber.d(it.toString()) }

}
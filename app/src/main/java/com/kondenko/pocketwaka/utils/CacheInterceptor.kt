package com.kondenko.pocketwaka.utils

import android.content.Context
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


class CacheInterceptor(val context: Context) : Interceptor {

    private val MAX_AGE = 60 * 60 * 5
    private val DAYS_VALID = 7

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.method() == "GET") {
            val name = "Cache-Control"
            val value: String
            if (Utils.isConnectionAvailable(context)) {
                value = "public, max-age=" + MAX_AGE
            } else {
                val maxStale = 60 * 60 * 24 * DAYS_VALID
                value = "public, only-if-cached, max-stale=" + maxStale
            }
            request = request.newBuilder().header(name, value).build()
        }
        return chain.proceed(request)
    }
}
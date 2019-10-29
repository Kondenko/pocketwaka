package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.HttpURLConnection

class UnauthorizedAccessInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response =
            chain.proceed(chain.request()).let {
                if (it.code() == HttpURLConnection.HTTP_UNAUTHORIZED) throw UnauthorizedException(it.message())
                else it
            }

}
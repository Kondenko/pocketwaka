package com.kondenko.pocketwaka.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {

    private val TAG = "HTTP"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i(TAG, request.toString())
        Log.i(TAG, "Request headers: ${request.headers()}")
        val response = chain.proceed(request)
        Log.i(TAG, response.toString())
        Log.i(TAG, "Response headers: ${response.headers()}")
        return response
    }
}
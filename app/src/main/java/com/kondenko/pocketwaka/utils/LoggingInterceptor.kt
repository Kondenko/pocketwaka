package com.kondenko.pocketwaka.utils

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.i("HTTP", "Request: $request")
        Log.i("HTTP", "Request header: ${request.headers()}")
        val response = chain.proceed(request)
        Log.i("HTTP", "Responce: $response")
        Log.i("HTTP", "Responce header: ${response.headers()}")
        return response
    }
}
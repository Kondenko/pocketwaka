package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ConnectivityStatusProvider(context: Context) {

    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    fun isNetworkAvailable() =
            connectivityManager?.let { manager ->
                val subject = PublishSubject.create<Boolean>()
                val networkRequest = NetworkRequest.Builder().build()
                val callback = object : ConnectivityManager.NetworkCallback() {
                    override fun onUnavailable() {
                        super.onUnavailable()
                        subject.onNext(false)
                    }

                    override fun onAvailable(network: Network?) {
                        super.onAvailable(network)
                        subject.onNext(true)
                    }
                }
                return@let subject
                        .doOnSubscribe { manager.registerNetworkCallback(networkRequest, callback) }
                        .doOnDispose { manager.unregisterNetworkCallback(callback) }
            } ?: Observable.error(NullPointerException("ConnectivityManager not available"))

}
package com.kondenko.pocketwaka.data.android

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class AppConnectivityStatusProvider(private val connectivityManager: ConnectivityManager?) : ConnectivityStatusProvider {

    override fun isNetworkAvailable() = connectivityManager?.let { manager ->
        val isNetworkInitiallyAvailable = manager.activeNetworkInfo?.isConnected == true
        val subject = BehaviorSubject.createDefault<Boolean>(isNetworkInitiallyAvailable)
        val networkRequest = NetworkRequest.Builder().build()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network?) {
                super.onLost(network)
                subject.onNext(false)
            }

            override fun onAvailable(network: Network?) {
                super.onAvailable(network)
                subject.onNext(true)
            }
        }
        subject
                .doOnSubscribe { manager.registerNetworkCallback(networkRequest, callback) }
                .doOnDispose { manager.unregisterNetworkCallback(callback) }
                .distinctUntilChanged()
    } ?: Observable.error(NullPointerException("ConnectivityManager is not available"))

}
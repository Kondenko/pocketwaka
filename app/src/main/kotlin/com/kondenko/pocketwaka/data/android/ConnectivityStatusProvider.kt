package com.kondenko.pocketwaka.data.android

import io.reactivex.Observable

interface ConnectivityStatusProvider {
    fun isNetworkAvailable(): Observable<Boolean>
}
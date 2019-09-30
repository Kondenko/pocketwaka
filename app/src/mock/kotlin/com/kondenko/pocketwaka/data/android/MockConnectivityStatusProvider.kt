package com.kondenko.pocketwaka.data.android

import io.reactivex.Observable

class MockConnectivityStatusProvider : ConnectivityStatusProvider {

    override fun isNetworkAvailable(): Observable<Boolean> =
            Observable.just(false)

}
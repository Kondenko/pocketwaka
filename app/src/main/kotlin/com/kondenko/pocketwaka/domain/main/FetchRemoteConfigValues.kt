package com.kondenko.pocketwaka.domain.main

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.exceptions.RemoteConfigFetchingException
import io.reactivex.Single

class FetchRemoteConfigValues(private val remoteConfig: FirebaseRemoteConfig, schedulers: SchedulersContainer)
    : UseCaseSingle<Nothing, Boolean>(schedulers) {

    override fun build(params: Nothing?): Single<Boolean> = Single.create<Boolean> { e ->
        remoteConfig.fetchAndActivate()
              .addOnSuccessListener { e.onSuccess(it) }
              .addOnFailureListener { e.tryOnError(RemoteConfigFetchingException(cause = it)) }
              .addOnCanceledListener { WakaLog.w("FetchRemoteConfigValues was canceled") }
    }

}
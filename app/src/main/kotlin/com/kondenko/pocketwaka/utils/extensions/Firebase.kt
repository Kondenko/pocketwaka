package com.kondenko.pocketwaka.utils.extensions

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.kondenko.pocketwaka.utils.exceptions.RemoteConfigException
import io.reactivex.Single

fun FirebaseRemoteConfig.getStringSingle(key: String) = this[key].asString().let {
    if (it != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING) Single.just(it)
    else Single.error(RemoteConfigException(key))
}

package com.kondenko.pocketwaka.utils.extensions

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import io.reactivex.Maybe

fun FirebaseRemoteConfig.getStringMaybe(key: String): Maybe<String> = this[key].asString().let {
    if (it != FirebaseRemoteConfig.DEFAULT_VALUE_FOR_STRING) Maybe.just(it)
    else Maybe.empty()
}

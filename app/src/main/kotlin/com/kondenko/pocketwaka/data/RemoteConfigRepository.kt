package com.kondenko.pocketwaka.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.utils.extensions.getLongMaybe
import com.kondenko.pocketwaka.utils.extensions.getOrNull
import com.kondenko.pocketwaka.utils.extensions.getStringMaybe
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.remoteconfig.remoteConfigDefaults
import io.reactivex.Maybe
import io.reactivex.rxkotlin.toMaybe

abstract class RemoteConfigRepository(private val remoteConfig: FirebaseRemoteConfig, private val prefs: SharedPreferences) {

    protected fun getString(key: String): Maybe<String> =
        getStringFromRemoteConfig(key)
            .switchIfEmpty(getStringFromPrefs(key))
            .defaultIfEmpty(remoteConfigDefaults[key] as String)
            .onErrorReturn {
                it.report("Error fetching a String from RemoteConfig")
                remoteConfigDefaults[key] as String
            }

    protected fun getLong(key: String): Maybe<Long> =
        getLongFromRemoteConfig(key)
            .switchIfEmpty(getLongFromPrefs(key))
            .defaultIfEmpty(remoteConfigDefaults[key] as Long)
            .onErrorReturn {
                it.report("Error fetching a Long from RemoteConfig")
                remoteConfigDefaults[key] as Long
            }

    private fun getStringFromPrefs(key: String): Maybe<String> =
        prefs.getString(key, null).toMaybe()

    private fun getStringFromRemoteConfig(key: String): Maybe<String> =
        remoteConfig.getStringMaybe(key)
            .doOnSuccess { value ->
                prefs.edit {
                    putString(key, value)
                }
            }

    private fun getLongFromPrefs(key: String): Maybe<Long> =
        prefs.getOrNull(key) { getLong(key, 0) }.toMaybe()

    private fun getLongFromRemoteConfig(key: String): Maybe<Long> =
        remoteConfig.getLongMaybe(key)
            .doOnSuccess { value ->
                prefs.edit {
                    putLong(key, value)
                }
            }

}
package com.kondenko.pocketwaka.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.utils.extensions.getStringMaybe
import io.reactivex.Maybe

abstract class RemoteConfigRepository(private val remoteConfig: FirebaseRemoteConfig, private val prefs: SharedPreferences) {

    protected operator fun get(key: String): Maybe<String> =
            getValueFromRemoteConfig(key).switchIfEmpty(getValueFromPrefs(key))

    private fun getValueFromPrefs(key: String): Maybe<String> =
            prefs.getString(key, null)
                    ?.let { Maybe.just(it) }
                    ?: Maybe.empty()

    private fun getValueFromRemoteConfig(key: String): Maybe<String> =
            remoteConfig.getStringMaybe(key)
                    .doOnSuccess { url ->
                        prefs.edit {
                            putString(key, url)
                        }
                    }

}
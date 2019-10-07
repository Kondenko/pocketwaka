package com.kondenko.pocketwaka.data.menu

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.utils.extensions.getStringSingle
import com.kondenko.pocketwaka.utils.remoteconfig.RemoteConfigKeys
import io.reactivex.Maybe
import io.reactivex.Single

class MenuRepository(private val remoteConfig: FirebaseRemoteConfig, private val prefs: SharedPreferences) {

    fun getGithubUrl(): Single<String> =
            getGithubUrlFromPrefs().switchIfEmpty(getGithubUrlFromRemoteConfig())

    private fun getGithubUrlFromPrefs(): Maybe<String> =
            prefs.getString(RemoteConfigKeys.githubUrl, null)
                    ?.let { Maybe.just(it) }
                    ?: Maybe.empty()

    private fun getGithubUrlFromRemoteConfig(): Single<String> =
            remoteConfig.getStringSingle(RemoteConfigKeys.githubUrl)
                    .doOnSuccess { url ->
                        prefs.edit {
                            putString(RemoteConfigKeys.githubUrl, url)
                        }
                    }

}
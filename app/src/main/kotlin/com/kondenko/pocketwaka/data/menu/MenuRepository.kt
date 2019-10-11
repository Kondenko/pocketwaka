package com.kondenko.pocketwaka.data.menu

import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.data.RemoteConfigRepository
import com.kondenko.pocketwaka.data.android.StringProvider
import com.kondenko.pocketwaka.utils.extensions.toOptionalSingle
import com.kondenko.pocketwaka.utils.remoteconfig.RemoteConfigKeys
import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Single

class MenuRepository(
        remoteConfig: FirebaseRemoteConfig,
        prefs: SharedPreferences,
        private val stringProvider: StringProvider
) : RemoteConfigRepository(remoteConfig, prefs) {

    fun getGithubUrl(): Single<KOptional<String>> =
            get(RemoteConfigKeys.githubUrl).toOptionalSingle()

    fun getSupportEmail(): Single<KOptional<String>> =
            get(RemoteConfigKeys.supportEmail).toOptionalSingle()

    fun getSupportEmailSubject(): String = stringProvider.getSupportEmailSubject()

}
package com.kondenko.pocketwaka.data.menu

import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.data.RemoteConfigRepository
import com.kondenko.pocketwaka.data.android.StringProvider
import com.kondenko.pocketwaka.utils.remoteconfig.RemoteConfigKeys
import io.reactivex.Single

class MenuRepository(
    remoteConfig: FirebaseRemoteConfig,
    prefs: SharedPreferences,
    private val stringProvider: StringProvider
) : RemoteConfigRepository(remoteConfig, prefs) {

    fun getPositiveReviewThreshold(): Single<Long> =
        getLong(RemoteConfigKeys.positiveReviewThreshold).toSingle()

    fun getGithubUrl(): Single<String> =
        getString(RemoteConfigKeys.githubUrl).toSingle()

    fun getSupportEmail(): Single<String> =
        getString(RemoteConfigKeys.supportEmail).toSingle()

    fun getSupportEmailSubject() =
        stringProvider.getSupportEmailSubject()

}
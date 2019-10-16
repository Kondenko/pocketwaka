package com.kondenko.pocketwaka.di.modules

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.kondenko.pocketwaka.domain.main.FetchRemoteConfigValues
import com.kondenko.pocketwaka.utils.remoteconfig.remoteConfigDefaults
import org.koin.dsl.module

val firebaseModule = module {
    single {
        FirebaseRemoteConfig.getInstance().apply {
            setDefaultsAsync(remoteConfigDefaults)
        }
    }
    single { FetchRemoteConfigValues(get(), get()) }
}
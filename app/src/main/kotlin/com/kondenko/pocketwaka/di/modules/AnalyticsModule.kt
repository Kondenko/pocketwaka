package com.kondenko.pocketwaka.di.modules

import com.google.firebase.analytics.FirebaseAnalytics
import com.kondenko.pocketwaka.analytics.ScreenTracker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single { FirebaseAnalytics.getInstance(androidContext()) }
    factory { ScreenTracker(get()) }
}
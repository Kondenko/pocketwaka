package com.kondenko.pocketwaka.di.modules

import android.preference.PreferenceManager
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.utils.date.DateProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { DateProvider() }
    single { ConnectivityStatusProvider(androidContext()) }
}
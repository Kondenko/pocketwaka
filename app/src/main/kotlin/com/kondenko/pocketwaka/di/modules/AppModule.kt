package com.kondenko.pocketwaka.di.modules

import android.preference.PreferenceManager
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.android.StringProvider
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.spannable.TimeSpannableCreator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { ConnectivityStatusProvider(androidContext()) }
    single { StringProvider(androidContext()) }
    single { DateProvider() }
    single { TimeSpannableCreator(androidContext()) }
    single { DateFormatter(context = androidContext(), stringProvider = get()) }
}
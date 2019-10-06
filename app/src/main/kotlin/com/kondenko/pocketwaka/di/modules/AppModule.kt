package com.kondenko.pocketwaka.di.modules

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleOwner
import com.kondenko.pocketwaka.data.android.AppConnectivityStatusProvider
import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.android.StringProvider
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.spannable.TimeSpannableCreator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { androidContext().getSystemService<ConnectivityManager>() }
    single<ConnectivityStatusProvider> { AppConnectivityStatusProvider(get()) }
    single { StringProvider(androidContext()) }
    single { DateProvider() }
    single { TimeSpannableCreator(androidContext()) }
    single { DateFormatter(context = androidContext(), stringProvider = get()) }
    factory { (context: Context, lifecycleOwner: LifecycleOwner) -> BrowserWindow(context, lifecycleOwner) }
}
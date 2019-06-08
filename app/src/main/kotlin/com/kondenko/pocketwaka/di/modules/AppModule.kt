package com.kondenko.pocketwaka.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.kondenko.pocketwaka.utils.TimeProvider
import io.reactivex.disposables.CompositeDisposable
import org.koin.dsl.module

object AppModule {

    fun create(context: Context) = module {
        single { PreferenceManager.getDefaultSharedPreferences(context) }
        single { TimeProvider() }
        single { CompositeDisposable() }
    }

}
package com.kondenko.pocketwaka.di.modules

import android.content.Context
import android.preference.PreferenceManager
import com.kondenko.pocketwaka.utils.TimeProvider
import org.koin.dsl.module.applicationContext

object AppModule {

    fun create(context: Context) = applicationContext {
        bean { PreferenceManager.getDefaultSharedPreferences(context) }
        bean { TimeProvider() }
    }

}
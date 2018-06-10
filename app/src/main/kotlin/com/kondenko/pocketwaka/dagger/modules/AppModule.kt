package com.kondenko.pocketwaka.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.kondenko.pocketwaka.dagger.PerApp
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {

    @Provides
    @PerApp
    fun provideContext() = context

    @Provides
    @PerApp
    fun provideSharedPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

}
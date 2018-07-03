package com.kondenko.pocketwaka.di

import android.content.Context
import com.kondenko.pocketwaka.di.modules.*

fun getModulesList(context: Context) = mutableListOf(
                AppModule.create(context),
                NetModule.create(context),
                MainModule.create(),
                AuthModule.create(),
                StatsModule.create(context)
        )

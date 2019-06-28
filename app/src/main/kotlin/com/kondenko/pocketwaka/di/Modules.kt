package com.kondenko.pocketwaka.di

import android.content.Context
import com.kondenko.pocketwaka.di.modules.*

fun getModuleList(context: Context) = mutableListOf(
                AppModule.create(context),
        NetModule.create(),
                PersistenceModule.create(context),
                MainModule.create(),
                AuthModule.create(),
                StatsModule.create(context)
        )

package com.kondenko.pocketwaka.di

import android.content.Context
import com.kondenko.pocketwaka.di.modules.MockAppModule
import com.kondenko.pocketwaka.di.modules.MockStatsModule

fun mockModulesList(context: Context): List<() -> org.koin.dsl.context.Context> = modulesList(context).apply {
    add(MockAppModule.create())
    add(MockStatsModule.create(context))
}
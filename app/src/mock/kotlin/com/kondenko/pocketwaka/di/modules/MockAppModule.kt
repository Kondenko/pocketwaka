package com.kondenko.pocketwaka.di.modules

import com.google.gson.GsonBuilder
import org.koin.dsl.module.applicationContext

object MockAppModule {
    fun create() = module {
        single { GsonBuilder().create() }
    }
}
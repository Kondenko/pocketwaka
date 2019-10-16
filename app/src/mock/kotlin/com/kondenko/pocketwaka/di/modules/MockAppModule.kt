package com.kondenko.pocketwaka.di.modules

import com.google.gson.GsonBuilder
import org.koin.dsl.module

val mockAppModule = module(override = true) {
    single { GsonBuilder().create() }
}
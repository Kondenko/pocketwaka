package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.auth.MockGetTokenHeaderValue
import org.koin.dsl.module

val mockAuthModule = module(override = true) {
    factory { MockGetTokenHeaderValue(get()) }
}
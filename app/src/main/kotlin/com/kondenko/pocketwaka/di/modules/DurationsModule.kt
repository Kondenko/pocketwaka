package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.branches.DurationsRepository
import com.kondenko.pocketwaka.data.branches.service.DurationsService
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.dsl.module

val durationsModule = module {
    single { getApiRetrofit().create<DurationsService>()}
    single { DurationsRepository(durationsService = get()) }
}
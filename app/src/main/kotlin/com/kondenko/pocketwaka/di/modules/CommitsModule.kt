package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.commits.CommitsRepository
import com.kondenko.pocketwaka.data.commits.service.CommitsService
import com.kondenko.pocketwaka.data.persistence.AppDatabase
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.dsl.module
import retrofit2.Retrofit

val commitsModule = module {
    factory { get<Retrofit>(Api).create<CommitsService>() }
    factory { get<AppDatabase>().commitsDao() }
    factory { CommitsRepository(get(), get(), get()) }
}
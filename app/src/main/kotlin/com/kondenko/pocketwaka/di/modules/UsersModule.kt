package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.user.UserService
import com.kondenko.pocketwaka.data.user.UsersRepository
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.user.HasPremiumFeatures
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.create

val usersModule = module {
    single {
        get<Retrofit>(Api).create<UserService>()
    }
    single {
        UsersRepository(get())
    }
    single {
        HasPremiumFeatures(
              schedulersContainer = get(),
              getTokenHeader = get<GetTokenHeaderValue>(),
              usersRepository = get()
        )
    }
}
package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.main.GetStoredAccessToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object MainModule {

    fun create() = module {
        factory { GetStoredAccessToken(get(), get(), get()) }
        factory { CheckIfUserIsLoggedIn(get(), get()) }
        factory { ClearCache(get(), get(), get()) }
        single { RefreshAccessToken(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { MainViewModel(get(), get(), get()) }
    }

}
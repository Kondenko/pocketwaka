package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.main.FetchRemoteConfigValues
import com.kondenko.pocketwaka.domain.main.MockCheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockMainModule = module(override = true) {
    factory {
        MockCheckIfUserIsLoggedIn(schedulers = get())
    }
    viewModel {
        MainViewModel(
              checkIfUserIsLoggedIn = get<MockCheckIfUserIsLoggedIn>(),
              clearCache = get<ClearCache>(),
              refreshAccessToken = get<RefreshAccessToken>(),
              fetchRemoteConfigValues = get<FetchRemoteConfigValues>()
        )
    }
}
package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.MockCheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.screens.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mockMainModule = module(override = true) {
    factory {
        MockCheckIfUserIsLoggedIn(schedulers = get())
    }
    viewModel { (defaultTabId: Int) ->
        MainViewModel(
              defaultTabId,
              checkIfUserIsLoggedIn = get<MockCheckIfUserIsLoggedIn>(),
              clearCache = get(),
              refreshAccessToken = get(),
              fetchRemoteConfigValues = get()
        )
    }
}
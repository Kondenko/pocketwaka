package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.DeleteSavedToken
import com.kondenko.pocketwaka.domain.main.GetStoredAccessToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainActivityPresenter
import org.koin.dsl.module

object MainModule {

    fun create() = module {
        factory { GetStoredAccessToken(get(), get(), get()) }
        factory { CheckIfUserIsLoggedIn(get(), get()) }
        factory { DeleteSavedToken(get(), get()) }
        single { RefreshAccessToken(get(), get(), get(), get(), get(), get(), get()) }
        single { MainActivityPresenter(get(), get(), get()) }
    }

}
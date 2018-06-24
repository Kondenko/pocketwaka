package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.DeleteSavedToken
import com.kondenko.pocketwaka.domain.main.GetStoredAccessToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainActivityPresenter
import org.koin.dsl.module.applicationContext

object MainModule {

    fun create() = applicationContext{
        factory { GetStoredAccessToken(get(), get(), get()) }
        factory { CheckIfUserIsLoggedIn(get(), get()) }
        factory { DeleteSavedToken(get(), get()) }
        bean { RefreshAccessToken(get(), get(), get(), get(), get(), get(), get()) }
        bean { MainActivityPresenter(get(), get(), get())}
    }

}
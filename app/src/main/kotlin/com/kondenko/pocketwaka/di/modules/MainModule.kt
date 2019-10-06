package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.domain.main.CheckIfUserIsLoggedIn
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.main.GetStoredAccessToken
import com.kondenko.pocketwaka.domain.main.RefreshAccessToken
import com.kondenko.pocketwaka.screens.main.MainViewModel
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {
    factory {
        GetStoredAccessToken(
                schedulers = get(),
                accessTokenRepository = get(),
                tokenEncryptor = get<TokenEncryptor>()
        )
    }
    factory {
        CheckIfUserIsLoggedIn(
                schedulers = get(),
                repository = get()
        )
    }
    factory {
        ClearCache(
                schedulers = get(),
                tokenRepository = get(),
                statsDao = get(),
                summaryDao = get()
        )
    }
    single {
        RefreshAccessToken(
                schedulers = get(),
                dateProvider = get(),
                tokenEncryptor = get<TokenEncryptor>(),
                accessTokenRepository = get(),
                getStoredAccessToken = get(),
                getAppId = get(),
                getAppSecret = get()
        )
    }
    viewModel {
        MainViewModel(
                checkIfUserIsLoggedIn = get(),
                clearCache = get(),
                refreshAccessToken = get()
        )
    }
}

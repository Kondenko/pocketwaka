package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.di.qualifiers.Auth
import com.kondenko.pocketwaka.domain.auth.*
import com.kondenko.pocketwaka.screens.login.LoginPresenter
import com.kondenko.pocketwaka.utils.encryption.StringEncryptor
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.dsl.module
import retrofit2.Retrofit

val authModule = module {
    factory { StringEncryptor() }
    factory { TokenEncryptor(stringEncryptor = get<StringEncryptor>()) }
    factory { EncryptedKeysRepository() }
    factory { get<Retrofit>(Auth).create<AccessTokenService>() }
    factory { AccessTokenRepository(service = get(), prefs = get(), gson = get()) }
    factory { GetAuthUrl(schedulers = get(), getAppId = get()) }
    factory {
        GetAppId(
            schedulers = get(),
            encryptedKeysRepository = get(),
            stringEncryptor = get<StringEncryptor>()
        )
    }
    factory {
        GetAppSecret(
            schedulers = get(),
            encryptedKeysRepository = get(),
            stringEncryptor = get<StringEncryptor>()
        )
    }
    factory {
        GetAccessToken(
            schedulers = get(),
            tokenEncryptor = get<TokenEncryptor>(),
            accessTokenRepository = get(),
            getAppId = get(),
            getAppSecret = get()
        )
    }
    factory {
        GetTokenHeaderValue(
            schedulers = get(),
            stringEncryptor = get<StringEncryptor>(),
            accessTokenRepository = get()
        )
    }
    single { LoginPresenter(getAuthUrl = get(), getAccessToken = get()) }
}
package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.di.Auth
import com.kondenko.pocketwaka.domain.auth.GetAccessToken
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.domain.auth.GetAuthUrl
import com.kondenko.pocketwaka.screens.auth.AuthPresenter
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.extensions.create
import org.koin.dsl.module
import retrofit2.Retrofit

object AuthModule {

    fun create() = module {
        single { Encryptor() }
        single { EncryptedKeysRepository() }
        single { get<Retrofit>(Auth).create<AccessTokenService>() }
        single { AccessTokenRepository(get(), get()) }
        factory { GetAuthUrl(get(), get()) }
        factory { GetAppId(get(), get(), get()) }
        factory { GetAppSecret(get(), get(), get()) }
        factory { GetAccessToken(get(), get(), get(), get(), get(),get()) }
        single { AuthPresenter(get() as GetAuthUrl, get() as GetAccessToken) }
    }

}
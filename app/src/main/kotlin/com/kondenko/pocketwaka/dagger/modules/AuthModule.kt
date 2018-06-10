package com.kondenko.pocketwaka.dagger.modules

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.qualifiers.Auth
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.utils.Encryptor
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class AuthModule {

    @Provides
    @PerApp
    fun provideEncryptor(): Encryptor = Encryptor()

    @Provides
    @PerApp
    fun provideEncryptedKeysRepository(): EncryptedKeysRepository = EncryptedKeysRepository()

    @Provides
    @PerApp
    fun provideAuthService(@Auth retrofit: Retrofit): AccessTokenService {
        return retrofit.create(AccessTokenService::class.java)
    }

}
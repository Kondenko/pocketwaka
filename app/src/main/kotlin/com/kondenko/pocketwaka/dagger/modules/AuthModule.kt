package com.kondenko.pocketwaka.dagger.modules

import com.kondenko.pocketwaka.dagger.qualifiers.Auth
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.utils.Encryptor
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class AuthModule {

    @Provides
    @Singleton
    fun provideEncryptor(): Encryptor = Encryptor()

    @Provides
    @Singleton
    fun provideEncryptedKeysRepository(): EncryptedKeysRepository = EncryptedKeysRepository()

    @Provides
    @Singleton
    fun provideAuthService(@Auth retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }

}
package com.kondenko.pocketwaka.dagger.module

import com.nhaarman.mockito_kotlin.mock
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class TestNetModule {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return mock()
    }

    @Provides
    @Singleton
    fun provideRetrofitForAuthentication(): Retrofit {
        return mock()
    }

}
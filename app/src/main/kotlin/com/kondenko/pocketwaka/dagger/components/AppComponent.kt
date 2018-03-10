package com.kondenko.pocketwaka.dagger.components

import android.content.Context
import android.content.SharedPreferences
import com.kondenko.pocketwaka.dagger.modules.AppModule
import com.kondenko.pocketwaka.dagger.modules.AuthModule
import com.kondenko.pocketwaka.dagger.modules.NetModule
import com.kondenko.pocketwaka.dagger.qualifiers.Api
import com.kondenko.pocketwaka.dagger.qualifiers.Auth
import com.kondenko.pocketwaka.dagger.qualifiers.Ui
import com.kondenko.pocketwaka.dagger.qualifiers.Worker
import com.kondenko.pocketwaka.data.auth.repository.EncryptedKeysRepository
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.utils.CacheInterceptor
import com.kondenko.pocketwaka.utils.Encryptor
import dagger.Component
import io.reactivex.Scheduler
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, NetModule::class, AuthModule::class))
interface AppComponent {

    fun getContext(): Context

    fun getSharedPreferences(): SharedPreferences

    fun getRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory

    fun getGsonConverterFactory(): GsonConverterFactory

    fun getCache(): Cache

    fun getCacheInterceptor(): CacheInterceptor

    fun getOkHttpClient(): OkHttpClient

    fun getRetrofitBuilder(): Retrofit.Builder

    @Auth
    fun getRetrofitForAuth(): Retrofit

    @Api
    fun getRetrofitForApi(): Retrofit

    @Worker
    fun getWorkerScheduler(): Scheduler

    @Ui
    fun getUiScheduler(): Scheduler

    fun getAuthService(): AccessTokenService

    fun getEncryptor(): Encryptor

    fun getEncryptedKeysRepository(): EncryptedKeysRepository

}
package com.kondenko.pocketwaka.dagger.module

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.utils.CacheInterceptor
import com.kondenko.pocketwaka.utils.LoggingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
open class NetModule(val context: Context) {

    @Provides
    @Singleton
    open fun provideHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        val cacheDirectory = File(context.cacheDir, "responses")
        val cacheSize: Long = 1024 * 512 // 512 Kb
        val cache = Cache(cacheDirectory, cacheSize)
        client.cache(cache)
        client.addInterceptor(CacheInterceptor(context))
        client.addInterceptor(LoggingInterceptor())
        return client.build()
    }

    @Provides
    @Singleton
    @Named(Const.URL_TYPE_AUTH)
    open fun provideRetrofitForAuthentication(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Const.URL_BASE)
                .client(okHttpClient)
                .build()
    }

    @Provides
    @Singleton
    @Named(Const.URL_TYPE_API)
    open fun provideRetrofitForApi(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Const.URL_API)
                .client(okHttpClient)
                .build()
    }


}
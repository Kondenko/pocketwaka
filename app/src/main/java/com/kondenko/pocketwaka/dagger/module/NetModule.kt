package com.kondenko.pocketwaka.dagger.module

import android.content.Context
import android.util.Log
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
import javax.inject.Singleton

@Module
class NetModule(val context: Context, val baseUrl: String) {

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val client = OkHttpClient.Builder()
        val cacheDirectory = File(context.cacheDir, "responses")
        val cacheSize: Long = 1024 * 512 // 512 Kb
        val cache = Cache(cacheDirectory, cacheSize)
        client.cache(cache)
        client.addInterceptor(CacheInterceptor(context))
//        client.addInterceptor(LoggingInterceptor())
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build()
    }


}
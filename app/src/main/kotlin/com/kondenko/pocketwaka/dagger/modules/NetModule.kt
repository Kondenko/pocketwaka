package com.kondenko.pocketwaka.dagger.modules

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.qualifiers.Api
import com.kondenko.pocketwaka.dagger.qualifiers.Auth
import com.kondenko.pocketwaka.dagger.qualifiers.Ui
import com.kondenko.pocketwaka.dagger.qualifiers.Worker
import com.kondenko.pocketwaka.utils.CacheInterceptor
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

@Module
class NetModule {

    @Provides
    @PerApp
    @Worker
    fun provideWorkerScheduler(): Scheduler = Schedulers.io()

    @Provides
    @PerApp
    @Ui
    fun provideUiScheduler(): Scheduler = AndroidSchedulers.mainThread()

    @Provides
    @PerApp
    fun provideCallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @Provides
    @PerApp
    fun provideGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    @PerApp
    fun provideOHttpCache(context: Context): Cache {
        val cacheDirectory = File(context.cacheDir, "responses")
        val cacheSize: Long = 1024 * 512 // 512 Kb
        return Cache(cacheDirectory, cacheSize)
    }

    @Provides
    @PerApp
    fun provideCacheInterceptor(context: Context): CacheInterceptor = CacheInterceptor(context)

    @Provides
    @PerApp
    fun provideHttpClient(cache: Cache, cacheInterceptor: CacheInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor(cacheInterceptor)
                .build()
    }

    @Provides
    @PerApp
    fun provideRetrofitBuilder(callAdapterFactory: RxJava2CallAdapterFactory, converterFactory: GsonConverterFactory, okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
                .client(okHttpClient)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
    }

    @Provides
    @PerApp
    @Auth
    fun provideRetrofitForAuthentication(builder: Retrofit.Builder): Retrofit = builder.baseUrl(Const.BASE_URL).build()

    @Provides
    @PerApp
    @Api
    fun provideRetrofitForApi(builder: Retrofit.Builder): Retrofit = builder.baseUrl(Const.URL_API).build()

}
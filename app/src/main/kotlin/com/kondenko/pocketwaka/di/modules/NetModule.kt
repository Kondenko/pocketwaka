package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.di.Api
import com.kondenko.pocketwaka.di.Auth
import com.kondenko.pocketwaka.di.Ui
import com.kondenko.pocketwaka.di.Worker
import com.kondenko.pocketwaka.utils.CacheInterceptor
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object NetModule {

    fun create(appContext: Context) = module {
        single(Worker) { Schedulers.io() }
        single(Ui) { AndroidSchedulers.mainThread() }
        single { SchedulersContainer(uiScheduler = get(Ui), workerScheduler = get(Worker)) }
        single { RxJava2CallAdapterFactory.create() }
        single { GsonConverterFactory.create() }
        single {
            val cacheDirectory = File(appContext.cacheDir, "responses")
            val cacheSize: Long = 1024 * 512 // 512 Kb
            Cache(cacheDirectory, cacheSize)
        }
        single { CacheInterceptor(appContext) }
        single {
            OkHttpClient.Builder()
                    .readTimeout(15, TimeUnit.SECONDS)
                    .cache(get())
                    .addInterceptor(get() as CacheInterceptor)
                    .build()
        }
        single {
            Retrofit.Builder()
                    .client(get())
                    .addCallAdapterFactory(get() as RxJava2CallAdapterFactory)
                    .addConverterFactory(get() as GsonConverterFactory)
        }
        single(Auth) { get<Retrofit.Builder>().baseUrl(Const.BASE_URL).build() }
        single(Api) { get<Retrofit.Builder>().baseUrl(Const.URL_API).build() }
    }

}
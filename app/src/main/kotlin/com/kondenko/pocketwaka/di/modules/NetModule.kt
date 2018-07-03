package com.kondenko.pocketwaka.di.modules

import android.content.Context
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.di.Api
import com.kondenko.pocketwaka.di.Auth
import com.kondenko.pocketwaka.di.Ui
import com.kondenko.pocketwaka.di.Worker
import com.kondenko.pocketwaka.utils.CacheInterceptor
import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File



object NetModule {

    fun create(appContext: Context) = applicationContext {
        bean(Worker) { Schedulers.io() }
        bean(Ui) { AndroidSchedulers.mainThread() }
        bean { SchedulerContainer(uiScheduler = get(Ui), workerScheduler = get(Worker)) }
        bean { RxJava2CallAdapterFactory.create() }
        bean {  GsonConverterFactory.create() }
        bean {
            val cacheDirectory = File(appContext.cacheDir, "responses")
            val cacheSize: Long = 1024 * 512 // 512 Kb
            Cache(cacheDirectory, cacheSize)
        }
        bean { CacheInterceptor(appContext)  }
        bean { OkHttpClient.Builder()
                .cache(get())
                .addInterceptor(get() as CacheInterceptor)
                .build()
        }
        bean { Retrofit.Builder()
                .client(get())
                .addCallAdapterFactory(get() as RxJava2CallAdapterFactory)
                .addConverterFactory(get() as GsonConverterFactory)
        }
        bean(Auth) { get<Retrofit.Builder>().baseUrl(Const.BASE_URL).build() }
        bean(Api) { get<Retrofit.Builder>().baseUrl(Const.URL_API).build() }
    }
}
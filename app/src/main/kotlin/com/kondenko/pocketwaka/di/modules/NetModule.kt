package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.di.qualifiers.Auth
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.utils.ResponseLogger
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val netModule = module {
    factory(Scheduler.Worker) { Schedulers.io() }
    factory(Scheduler.Ui) { AndroidSchedulers.mainThread() }
    factory { SchedulersContainer(uiScheduler = get(Scheduler.Ui), workerScheduler = get(Scheduler.Worker)) }
    single { RxJava2CallAdapterFactory.create() }
    single { GsonConverterFactory.create() }
    single {
        OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addNetworkInterceptor(ResponseLogger())
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

fun Scope.getApiRetrofit() = get<Retrofit>(Api)

package com.kondenko.pocketwaka.di.modules

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.di.qualifiers.Api
import com.kondenko.pocketwaka.di.qualifiers.Auth
import com.kondenko.pocketwaka.di.qualifiers.Scheduler
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.UnauthorizedAccessInterceptor
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.ifDebug
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    factory(Scheduler.Worker) { Schedulers.io() }
    factory(Scheduler.Ui) { AndroidSchedulers.mainThread() }
    factory { SchedulersContainer(uiScheduler = get(Scheduler.Ui), workerScheduler = get(Scheduler.Worker)) }
    single { RxJava2CallAdapterFactory.create() }
    single { GsonConverterFactory.create() }
    single {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) = WakaLog.d(message)
        }).apply {
            setLevel(HttpLoggingInterceptor.Level.BASIC)
        }
    }
    single {
        val cacheSizeBytes: Long = 1024 * 1024 * 4
        Cache(androidContext().cacheDir, cacheSizeBytes)
    }
    single {
        OkHttpClient.Builder()
              .connectTimeout(15, TimeUnit.SECONDS)
              .readTimeout(30, TimeUnit.SECONDS)
              .addNetworkInterceptor {
                  val maxAgeSec = TimeUnit.MINUTES.toSeconds(2) // read from cache for 60 seconds even if there is internet connection
                  it.proceed(it.request()).newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAgeSec")
                        .removeHeader("Pragma")
                        .build()
              }
              .addNetworkInterceptor {
                  it.proceed(it.request()).also {
                      val wasCached = it.cacheResponse != null
                      WakaLog.d("CACHED = $wasCached (${it.request.url})")
                  }
              }
              .cache(get())
              .addNetworkInterceptor(UnauthorizedAccessInterceptor())
              .addLoggingInterceptor(get<HttpLoggingInterceptor>())
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

private fun OkHttpClient.Builder.addLoggingInterceptor(loggingInterceptor: Interceptor) = apply {
    ifDebug {
        addNetworkInterceptor(loggingInterceptor)
    }
}

fun Scope.getApiRetrofit() = get<Retrofit>(Api)

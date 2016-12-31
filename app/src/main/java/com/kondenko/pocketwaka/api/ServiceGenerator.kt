package com.kondenko.pocketwaka.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Kondenko on 24.12.2016.
 */
object ServiceGenerator {

    private val httpClient = OkHttpClient.Builder()


//    val builder = Retrofit.Builder()
//            .baseUrl(API_BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()

/*
    fun <S> createService(serviceClass: Class<S>, code: String): S {
        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }
*/

    /*
    fun <S> createService(serviceClass: Class<S>): S {
        return createService(serviceClass, null)
    }



    fun <S> createService(serviceClass: Class<S>, username: String, password: String): S {
        val credentials = username + ":" + password
        val basic = "Basic " + Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val requestBuilder = original.newBuilder()
                    .header("Authorization", basic)
                    .header("Accept", "application/json")
                    .method(original.method(), original.body())

            val request = requestBuilder.build()
            chain.proceed(request)
        }

        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }


    fun <S> createService(serviceClass: Class<S>, token: AccessToken?): S {
        if (token != null) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Authorization",
                                token.getTokenType() + " " + token.getAccessToken())
                        .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }
        }

        val client = httpClient.build()
        val retrofit = builder.client(client).build()
        return retrofit.create(serviceClass)
    }
    */
}
package com.kondenko.pocketwaka.data.stats.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.stats.model.StatsDataWrapper
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface StatsService {

    @GET("users/current/stats/{range}")
    fun getCurrentUserStats(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Path("range") range: String
    ): Single<StatsDataWrapper>

}
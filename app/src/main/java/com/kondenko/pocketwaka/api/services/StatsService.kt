package com.kondenko.pocketwaka.api.services

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.model.stats.StatsDataWrapper
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import rx.Single

interface StatsService {

    @GET("users/current/stats/{range}")
    fun getCurrentUserStats(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Path("range") range: String
    ): Single<StatsDataWrapper>

}
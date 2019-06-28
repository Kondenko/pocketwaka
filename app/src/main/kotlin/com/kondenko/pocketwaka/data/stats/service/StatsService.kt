package com.kondenko.pocketwaka.data.stats.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.stats.model.StatsServiceResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

private const val PATH_RANGE = "range"

interface StatsService {

    @GET("users/current/stats/{$PATH_RANGE}")
    fun getCurrentUserStats(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Path(PATH_RANGE) range: String
    ): Single<StatsServiceResponse>

}
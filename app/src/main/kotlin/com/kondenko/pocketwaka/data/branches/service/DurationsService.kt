package com.kondenko.pocketwaka.data.branches.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.branches.model.DurationsServerModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DurationsService {

    @GET("users/current/durations")
    fun getDurations(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Query("date") date: String,
            @Query("project") project: String?,
            @Query("branches") branches: String?
    ): Single<DurationsServerModel>

}
package com.kondenko.pocketwaka.data.summary.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.summary.model.Summary
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SummaryService {

    @GET("users/current/summaries")
    fun getSummaries(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Query("start") start: String,
            @Query("end") end: String,
            @Query("project") project: String? = null,
            @Query("branches") branches: String? = null
    ): Single<Summary>

}
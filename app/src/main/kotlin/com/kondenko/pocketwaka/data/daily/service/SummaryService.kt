package com.kondenko.pocketwaka.data.daily.service

import com.kondenko.pocketwaka.data.daily.dto.Summary
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.GET

interface SummaryService {

    @GET("/api/v1/users/current/summaries")
    fun getSummaries(
            @Field("start") start: String,
            @Field("end") end: String,
            project: String? = null,
            branches: String? = null
    ): Single<Summary>

}
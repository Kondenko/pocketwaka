package com.kondenko.pocketwaka.data.commits.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.commits.model.CommitsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val projectPathVariable = "project"

interface CommitsService {

    @GET("users/current/projects/{$projectPathVariable}/commits")
    fun getCommits(
            @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
            @Path(projectPathVariable) project: String? = null,
            @Query("author") author: String? = null,
            @Query("branch") branch: String? = null,
            @Query("page") page: Int? = null
    ): Single<CommitsResponse>

}
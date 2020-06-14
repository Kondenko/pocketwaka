package com.kondenko.pocketwaka.data.user

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.user.model.UserResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

private const val PATH_USER = "user"

interface UserService {

    companion object {
        const val CURRENT = "current"
    }

    @GET("users/{$PATH_USER}")
    fun getUser(
          @Header(Const.HEADER_BEARER_NAME) tokenHeaderValue: String,
          @Path(PATH_USER) id: String = CURRENT
    ): Single<UserResponse>

}
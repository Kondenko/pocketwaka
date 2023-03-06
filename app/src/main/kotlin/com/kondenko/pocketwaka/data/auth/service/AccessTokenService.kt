package com.kondenko.pocketwaka.data.auth.service

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

const val TOKEN_URL_POSTFIX = "oauth/token"

interface AccessTokenService {

    @FormUrlEncoded
    @POST(TOKEN_URL_POSTFIX)
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String,
            @Field("code") code: String
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST(TOKEN_URL_POSTFIX)
    fun getRefreshToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String,
            @Field("refresh_token") refreshToken: String
    ): Single<AccessToken>

}
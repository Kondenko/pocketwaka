package com.kondenko.pocketwaka.data.auth.service

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AccessTokenService {

    @FormUrlEncoded
    @POST(Const.TOKEN_URL_POSTFIX)
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String,
            @Field("code") code: String
    ): Single<AccessToken>

    @FormUrlEncoded
    @POST(Const.TOKEN_URL_POSTFIX)
    fun getRefreshToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String,
            @Field("refresh_token") refreshToken: String
    ): Single<AccessToken>

}
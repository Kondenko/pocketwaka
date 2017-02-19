package com.kondenko.pocketwaka.api.services

import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.api.oauth.AccessToken
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST
import rx.Observable

interface LoginService {

    @Headers(Const.HEADER_ACCEPT)
    @FormUrlEncoded
    @POST(Const.TOKEN_URL_POSTFIX)
    fun getAccessToken(
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String,
            @Field("code") code: String
    ): Observable<AccessToken>

}
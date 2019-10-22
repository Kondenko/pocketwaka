package com.kondenko.pocketwaka.data.auth.model.server

import com.google.gson.annotations.SerializedName

data class AccessToken(
        @SerializedName("access_token")
        var accessToken: String,
        @SerializedName("expires_in")
        var expiresIn: Double,
        @SerializedName("refresh_token")
        var refreshToken: String,
        @SerializedName("scope")
        var scope: String,
        @SerializedName("token_type")
        var tokenType: String,
        @SerializedName("uid")
        var uid: String,
        @SerializedName("created_at")
        var createdAt: Float
) {

    fun isValid(currentTimeSec: Float) = createdAt + expiresIn > currentTimeSec

}
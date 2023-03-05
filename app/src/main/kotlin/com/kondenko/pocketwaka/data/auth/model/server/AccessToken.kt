package com.kondenko.pocketwaka.data.auth.model.server

import com.google.gson.annotations.SerializedName
import com.kondenko.pocketwaka.utils.extensions.toZonedDateTime
import org.threeten.bp.*

data class AccessToken(
    @SerializedName("access_token")
    var accessToken: String,
    @SerializedName("refresh_token")
    var refreshToken: String,
    @SerializedName("expires_in")
    var expiresIn: Double,
    @SerializedName("expires_at")
    var expiresAt: ZonedDateTime,
    @SerializedName("scope")
    var scope: String,
    @SerializedName("token_type")
    var tokenType: String,
    @SerializedName("uid")
    var uid: String,
) {

    fun isValid(currentTimeSec: Long): Boolean =
        currentTimeSec.toZonedDateTime().isBefore(expiresAt)
}
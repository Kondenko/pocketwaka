package com.kondenko.pocketwaka.data.auth.model

data class AccessToken(
        var accessToken: String,
        var expiresIn: Double,
        var refreshToken: String,
        var scope: String,
        var tokenType: String,
        var uid: String,
        var createdAt: Float) {

    fun isValid(currentTimeSec: Float) = createdAt + expiresIn > currentTimeSec

}
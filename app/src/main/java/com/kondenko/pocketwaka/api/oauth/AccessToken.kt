package com.kondenko.pocketwaka.api.oauth

data class AccessToken(
        var access_token: String,
        var expires_in: Double,
        var refresh_token: String,
        var scope: String,
        var token_type: String,
        var uid: String
)
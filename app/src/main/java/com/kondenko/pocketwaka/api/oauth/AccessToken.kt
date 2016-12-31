package com.kondenko.pocketwaka.api.oauth

data class AccessToken(
        val access_token: String,
        val expires_in: Double,
        val refresh_token: String,
        val scope: String,
        val token_type: String,
        val uid: String
)
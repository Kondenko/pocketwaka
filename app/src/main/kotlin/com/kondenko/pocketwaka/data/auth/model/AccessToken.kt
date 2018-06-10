package com.kondenko.pocketwaka.data.auth.model

import com.kondenko.pocketwaka.utils.currentTimeSec

data class  AccessToken(
        var access_token: String,
        var expires_in: Double,
        var refresh_token: String,
        var scope: String,
        var token_type: String,
        var uid: String,
        var created_at: Float) {

    fun isValid() = created_at + expires_in > currentTimeSec()

}
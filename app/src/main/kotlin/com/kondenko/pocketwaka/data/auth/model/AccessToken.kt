package com.kondenko.pocketwaka.data.auth.model

import com.kondenko.pocketwaka.utils.Utils

open class AccessToken(
        open var access_token: String,
        open var expires_in: Double,
        open var refresh_token: String,
        open var scope: String,
        open var token_type: String,
        open var uid: String,
        open var created_at: Float) {

    open fun isValid(): Boolean {
        val expirationTime = created_at + expires_in
        val currentTime = Utils.currentTimeSec()
        return expirationTime > currentTime
    }

    override fun toString(): String {
        return "AccessToken: { access_token: $access_token, expires_in: $expires_in, refresh_token: $refresh_token, scope: $scope, token_type: $token_type, uid: $uid"
    }
}
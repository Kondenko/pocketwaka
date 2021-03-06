package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import com.kondenko.pocketwaka.utils.extensions.getStringOrThrow
import com.kondenko.pocketwaka.utils.extensions.singleOrErrorIfNull
import io.reactivex.Completable
import io.reactivex.Single

class AccessTokenRepository(private val service: AccessTokenService, private val prefs: SharedPreferences) {

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_SCOPE = "scope"
    private val KEY_TOKEN_TYPE = "token_type"
    private val KEY_UID = "uid"
    private val KEY_CREATED_AT = "created_at"

    fun getNewAccessToken(id: String, secret: String, redirectUri: String, grantType: String, code: String) =
            service.getAccessToken(id, secret, redirectUri, grantType, code)

    fun getRefreshedAccessToken(clientId: String, clientSecret: String, redirectUri: String, grantType: String, refreshToken: String) =
            service.getRefreshToken(clientId, clientSecret, redirectUri, grantType, refreshToken)

    fun getEncryptedToken(): Single<AccessToken> {
        return isTokenSaved().flatMap { isSaved ->
            if (!isSaved) Single.error(UnauthorizedException("Couldn't obtain an AccessToken object from preferences"))
            else Single.just(
                    AccessToken(
                            accessToken = prefs.getStringOrThrow(KEY_ACCESS_TOKEN),
                            refreshToken = prefs.getStringOrThrow(KEY_REFRESH_TOKEN),
                            tokenType = prefs.getStringOrThrow(KEY_TOKEN_TYPE),
                            scope = prefs.getStringOrThrow(KEY_SCOPE),
                            uid = prefs.getStringOrThrow(KEY_UID),
                            expiresIn = prefs.getFloat(KEY_EXPIRES_IN, 0f).toDouble(),
                            createdAt = prefs.getFloat(KEY_CREATED_AT, 0f)
                    )
            )
        }
    }

    fun getRefreshToken() = prefs.getString(KEY_REFRESH_TOKEN, null)
            .singleOrErrorIfNull(UnauthorizedException("No refresh token found in preferences"))

    fun getEncryptedTokenValue() = prefs.getString(KEY_ACCESS_TOKEN, null)
            .singleOrErrorIfNull(UnauthorizedException("Couldn't obtain an access token string from preferences"))

    fun saveToken(token: AccessToken, createdAt: Float) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token.accessToken)
            putFloat(KEY_EXPIRES_IN, token.expiresIn.toFloat())
            putString(KEY_REFRESH_TOKEN, token.refreshToken)
            putString(KEY_SCOPE, token.scope)
            putString(KEY_TOKEN_TYPE, token.tokenType)
            putString(KEY_UID, token.uid)
            putFloat(KEY_CREATED_AT, createdAt)
        }
    }

    fun deleteToken(): Completable {
        return Completable.fromRunnable {
            prefs.edit {
                remove(KEY_ACCESS_TOKEN)
                remove(KEY_EXPIRES_IN)
                remove(KEY_REFRESH_TOKEN)
                remove(KEY_SCOPE)
                remove(KEY_TOKEN_TYPE)
                remove(KEY_UID)
                remove(KEY_CREATED_AT)
            }
        }
    }

    fun isTokenSaved(): Single<Boolean> = Single.just(prefs.contains(KEY_ACCESS_TOKEN))

}
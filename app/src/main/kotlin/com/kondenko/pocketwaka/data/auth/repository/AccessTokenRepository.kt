package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AuthService
import com.kondenko.pocketwaka.utils.edit
import io.reactivex.Single
import javax.inject.Inject

@PerApp
class AccessTokenRepository @Inject constructor(private val service: AuthService, private val prefs: SharedPreferences) {

    private val KEY_ACCESS_TOKEN = "access_token"
    private val KEY_EXPIRES_IN = "expires_in"
    private val KEY_REFRESH_TOKEN = "refresh_token"
    private val KEY_SCOPE = "scope"
    private val KEY_TOKEN_TYPE = "token_type"
    private val KEY_UID = "uid"
    private val KEY_CREATED_AT = "created_at"

    fun getNewAccessToken(id: String, secret: String, redirectUri: String, grantType: String, code: String): Single<AccessToken> {
        return service.getAccessToken(id, secret, redirectUri, grantType, code)
    }

    fun getEncryptedToken(): Single<AccessToken> {
        return isTokenSaved().flatMap { isSaved ->
            if (isSaved) Single.error(IllegalStateException("Access Token is not acquired yet"))
            else Single.just(
                    AccessToken(
                            prefs.getString(KEY_ACCESS_TOKEN, null),
                            prefs.getFloat(KEY_EXPIRES_IN, 0f).toDouble(),
                            prefs.getString(KEY_REFRESH_TOKEN, null),
                            prefs.getString(KEY_SCOPE, null),
                            prefs.getString(KEY_TOKEN_TYPE, null),
                            prefs.getString(KEY_UID, null),
                            prefs.getFloat(KEY_CREATED_AT, 0f))
            )
        }
    }

//    fun getTokenHeaderValue() = Const.HEADER_BEARER_VALUE_PREFIX + " " + getEncryptedAccessTOken()

    fun saveToken(token: AccessToken, createdAt: Float) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token.access_token)
            putFloat(KEY_EXPIRES_IN, token.expires_in.toFloat())
            putString(KEY_REFRESH_TOKEN, token.refresh_token)
            putString(KEY_SCOPE, token.scope)
            putString(KEY_TOKEN_TYPE, token.token_type)
            putString(KEY_UID, token.uid)
            putFloat(KEY_CREATED_AT, createdAt)
        }
    }

    fun deleteToken() {
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

    fun isTokenSaved() = Single.just(prefs.contains(KEY_ACCESS_TOKEN))

    fun getEncryptedAccessTOken() = prefs.getString(KEY_ACCESS_TOKEN, null)

}
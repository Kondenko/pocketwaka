package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import com.kondenko.pocketwaka.utils.extensions.getStringOrThrow
import com.kondenko.pocketwaka.utils.extensions.singleOrErrorIfNull
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.ZonedDateTime
import java.net.URLDecoder

private const val KEY_ACCESS_TOKEN = "access_token"
private const val KEY_EXPIRES_IN = "expires_in"
private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_SCOPE = "scope"
private const val KEY_TOKEN_TYPE = "token_type"
private const val KEY_UID = "uid"

@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
const val KEY_EXPIRES_AT = "expires_at"

class AccessTokenRepository(
    private val service: AccessTokenService,
    private val gson: Gson,
    private val prefs: SharedPreferences
) {

    fun getNewAccessToken(
        id: String,
        secret: String,
        redirectUri: String,
        grantType: String,
        code: String
    ): Single<AccessToken> = service.getAccessToken(id, secret, redirectUri, grantType, code)
        .map { response ->
            when (val contentType = response.contentType()?.subtype) {
                "json" -> response.string().parseJson()
                "html" -> response.string().parseHtml()
                else -> throw RuntimeException("Unsupported access token response type: $contentType")
            }
        }

    private fun String.parseJson(): AccessToken = gson.fromJson(this, AccessToken::class.java)

    private fun String.parseHtml(): AccessToken =
        split("&").associate { keyValue ->
            val equalsIndex = keyValue.indexOf("=")
            val key = keyValue.substring(0, equalsIndex)
            val value = keyValue.substring(equalsIndex + 1)
            key to value
        }.let { map ->
            AccessToken(
                accessToken = requireNotNull(map["access_token"]),
                refreshToken = requireNotNull(map["refresh_token"]),
                tokenType = requireNotNull(map["token_type"]),
                scope = requireNotNull(map["scope"]).decodeHtml(),
                uid = requireNotNull(map["uid"]),
                expiresIn = requireNotNull(map["expires_in"]).toDouble(),
                expiresAt = requireNotNull(map["expires_at"])
                    .decodeHtml()
                    .let(ZonedDateTime::parse),
            )
        }

    fun getRefreshedAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        grantType: String,
        refreshToken: String
    ) = service.getRefreshToken(clientId, clientSecret, redirectUri, grantType, refreshToken)

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
                    expiresAt = prefs.getString(KEY_EXPIRES_AT, null)
                        ?.decodeHtml()
                        .let(ZonedDateTime::parse)
                )
            )
        }
    }

    fun getRefreshToken() = prefs.getString(KEY_REFRESH_TOKEN, null)
        .singleOrErrorIfNull(UnauthorizedException("No refresh token found in preferences"))

    fun getEncryptedTokenValue() = prefs.getString(KEY_ACCESS_TOKEN, null)
        .singleOrErrorIfNull(UnauthorizedException("Couldn't obtain an access token string from preferences"))

    fun saveToken(token: AccessToken) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, token.accessToken)
            putFloat(KEY_EXPIRES_IN, token.expiresIn.toFloat())
            putString(KEY_REFRESH_TOKEN, token.refreshToken)
            putString(KEY_SCOPE, token.scope)
            putString(KEY_TOKEN_TYPE, token.tokenType)
            putString(KEY_UID, token.uid)
            putString(KEY_EXPIRES_AT, token.expiresAt.toString())
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
                remove(KEY_EXPIRES_AT)
            }
        }
    }

    fun isTokenSaved(): Single<Boolean> = Single.just(prefs.contains(KEY_ACCESS_TOKEN))

    private fun String.decodeHtml() = URLDecoder.decode(this, Charsets.UTF_8.name())

}
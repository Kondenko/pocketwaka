package com.kondenko.pocketwaka.utils

import android.util.Base64
import com.kondenko.pocketwaka.data.auth.model.AccessToken

class Encryptor {

    fun encryptToken(token: AccessToken) = AccessToken(
            accessToken = encrypt(token.accessToken),
            expiresIn = token.expiresIn,
            refreshToken = encrypt(token.refreshToken),
            scope = encrypt(token.scope),
            tokenType = encrypt(token.tokenType),
            uid = encrypt(token.uid),
            createdAt = token.createdAt
    )

    fun decryptToken(token: AccessToken) = AccessToken(
            accessToken = decrypt(token.accessToken),
            expiresIn = token.expiresIn,
            refreshToken = decrypt(token.refreshToken),
            scope = decrypt(token.scope),
            tokenType = decrypt(token.tokenType),
            uid = decrypt(token.uid),
            createdAt = token.createdAt
    )

    fun encrypt(value: String): String = Base64.encodeToString(value.toByteArray(), Base64.DEFAULT)

    fun decrypt(value: String): String = String(Base64.decode(value, Base64.DEFAULT))

}


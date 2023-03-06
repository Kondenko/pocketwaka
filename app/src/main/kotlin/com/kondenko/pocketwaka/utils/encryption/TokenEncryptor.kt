package com.kondenko.pocketwaka.utils.encryption

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken

class TokenEncryptor(private val stringEncryptor: Encryptor<String>) : Encryptor<AccessToken> {

    override fun encrypt(token: AccessToken) = AccessToken(
        accessToken = stringEncryptor.encrypt(token.accessToken),
        refreshToken = stringEncryptor.encrypt(token.refreshToken),
        expiresAt = token.expiresAt,
    )

    override fun decrypt(token: AccessToken) = AccessToken(
        accessToken = stringEncryptor.decrypt(token.accessToken),
        refreshToken = stringEncryptor.decrypt(token.refreshToken),
        expiresAt = token.expiresAt,
    )

}


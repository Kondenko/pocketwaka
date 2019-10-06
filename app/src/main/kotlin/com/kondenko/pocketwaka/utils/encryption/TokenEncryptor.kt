package com.kondenko.pocketwaka.utils.encryption

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken

class TokenEncryptor(private val stringEncryptor: Encryptor<String>) : Encryptor<AccessToken> {

    override fun encrypt(token: AccessToken) = AccessToken(
            accessToken = stringEncryptor.encrypt(token.accessToken),
            expiresIn = token.expiresIn,
            refreshToken = stringEncryptor.encrypt(token.refreshToken),
            scope = stringEncryptor.encrypt(token.scope),
            tokenType = stringEncryptor.encrypt(token.tokenType),
            uid = stringEncryptor.encrypt(token.uid),
            createdAt = token.createdAt
    )

    override fun decrypt(token: AccessToken) = AccessToken(
            accessToken = stringEncryptor.decrypt(token.accessToken),
            expiresIn = token.expiresIn,
            refreshToken = stringEncryptor.decrypt(token.refreshToken),
            scope = stringEncryptor.decrypt(token.scope),
            tokenType = stringEncryptor.decrypt(token.tokenType),
            uid = stringEncryptor.decrypt(token.uid),
            createdAt = token.createdAt
    )

}


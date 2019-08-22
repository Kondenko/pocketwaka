package com.kondenko.pocketwaka.testutils

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken

/**
 * Returns an access token with default values.
 * Note: values should be the same as in [com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository.getEncryptedToken] for tests to pass.
 */
fun getAccessTokenMock(defaultStringValue: String = "") = AccessToken(
        accessToken = defaultStringValue,
        expiresIn = 0.0,
        refreshToken = defaultStringValue,
        scope = defaultStringValue,
        tokenType = defaultStringValue,
        uid = defaultStringValue,
        createdAt = 0f
)

package com.kondenko.pocketwaka.testutils

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import org.threeten.bp.ZonedDateTime

/**
 * Returns an access token with default values.
 * Note: values should be the same as in [com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository.getEncryptedToken] for tests to pass.
 */
fun getAccessTokenMock(defaultStringValue: String = "", expiresAt: ZonedDateTime) = AccessToken(
        accessToken = defaultStringValue,
        refreshToken = defaultStringValue,
        expiresAt = expiresAt,
)

package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import com.kondenko.pocketwaka.utils.extensions.toSingle
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test
import org.threeten.bp.ZonedDateTime

class GetStoredAccessTokenTest {

    private val tokenEncryptor: TokenEncryptor = mock()

    private val repository: AccessTokenRepository = mock()

    private val getAccessToken = GetStoredAccessToken(testSchedulers, repository, tokenEncryptor)

    @Test
    fun `should decrypt access token`() {
        val encryptedToken: AccessToken = AccessToken(
            accessToken = "encrypted",
            refreshToken = "encrypted",
            expiresIn = 0.0,
            expiresAt = ZonedDateTime.now(),
            scope = "encrypted",
            tokenType = "encrypted",
            uid = "encrypted",
        )
        val decryptedToken: AccessToken = AccessToken(
            accessToken = "decrypted",
            refreshToken = "decrypted",
            expiresIn = 0.0,
            expiresAt = ZonedDateTime.now(),
            scope = "decrypted",
            tokenType = "decrypted",
            uid = "decrypted",
        )

        whenever(repository.getEncryptedToken()).doReturn(encryptedToken.toSingle())
        whenever(tokenEncryptor.decrypt(encryptedToken)).doReturn(decryptedToken)

        val single = getAccessToken()

        verify(repository).getEncryptedToken()
        verify(tokenEncryptor).decrypt(encryptedToken)

        with(single.test()) {
            assertValue { it == decryptedToken && it != encryptedToken }
            assertSubscribed()
            assertNoErrors()
            assertComplete()
        }
    }

}
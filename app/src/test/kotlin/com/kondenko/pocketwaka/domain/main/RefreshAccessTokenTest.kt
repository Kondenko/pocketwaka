package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import com.kondenko.pocketwaka.utils.extensions.toSingle
import com.kondenko.pocketwaka.utils.extensions.toZonedDateTime
import com.nhaarman.mockito_kotlin.*
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.ZonedDateTime

@RunWith(MockitoJUnitRunner::class)
class RefreshAccessTokenTest {

    private val dateProvider: DateProvider = mock()

    private val tokenEncryptor: TokenEncryptor = mock()

    private val accessTokenRepository: AccessTokenRepository = mock()

    private val getStoredAccessToken: GetStoredAccessToken = mock()

    private val getAppId: GetAppId = mock()

    private val getAppSecret: GetAppSecret = mock()

    private val useCase = RefreshAccessToken(
        testSchedulers,
        dateProvider,
        tokenEncryptor,
        accessTokenRepository,
        getStoredAccessToken,
        getAppId,
        getAppSecret
    )

    lateinit var token: AccessToken

    @Before
    fun before() {
        token = AccessToken(
            "ACCESS_TOKEN",
            "REFRESH_TOKEN",
            ZonedDateTime.now(),
        )
    }

    @Test
    fun `token should be valid`() {
        val currentTime = dateProvider.getCurrentTimeMillis()
        val expiresAt = currentTime.toZonedDateTime().plusDays(1)
        val token = token.copy(expiresAt = expiresAt)
        assertTrue(token.isValid(currentTime))
    }

    @Test
    fun `token should NOT be valid`() {
        val currentTime = dateProvider.getCurrentTimeMillis()
        val expiresAt = currentTime.toZonedDateTime().minusDays(1)
        val token = token.copy(expiresAt = expiresAt)
        assertFalse(token.isValid(currentTime))
    }

    @Test
    fun `should return token if it's valid`() {
        val token: AccessToken = mock()
        val currentTime = 500L

        whenever(getStoredAccessToken.build()).doReturn(token.toSingle())
        whenever(dateProvider.getCurrentTimeSec()).doReturn(currentTime)
        whenever(token.isValid(currentTime)).doReturn(true)

        val single = useCase.invoke()
        verifyNoMoreInteractions(getAppId)
        verifyNoMoreInteractions(getAppSecret)
        verifyNoMoreInteractions(accessTokenRepository)
        verifyNoMoreInteractions(tokenEncryptor)
        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
            assertValue(token)
        }
    }

    @Test
    fun `should get a new refresh token if access token is not valid`() {
        val invalidToken: AccessToken = mock()
        val newToken: AccessToken = mock()
        val encryptedNewToken: AccessToken = mock()
        val currentTime = 500L
        val appId = "foo"
        val appSecret = "bar"
        val refreshToken = "baz"

        whenever(getStoredAccessToken.build()).doReturn(invalidToken.toSingle())
        whenever(dateProvider.getCurrentTimeSec()).doReturn(currentTime)
        whenever(invalidToken.isValid(anyLong())).doReturn(false)
        whenever(getAppId.build()).doReturn(appId.toSingle())
        whenever(getAppSecret.build()).doReturn(appSecret.toSingle())
        whenever(accessTokenRepository.getRefreshToken()).doReturn(refreshToken.toSingle())
        whenever(tokenEncryptor.encrypt(newToken)).doReturn(encryptedNewToken)
        whenever(accessTokenRepository.getRefreshedAccessToken(
            clientId = eq(appId),
            clientSecret = eq(appSecret),
            redirectUri = anyString(),
            grantType = anyString(),
            refreshToken = eq(refreshToken)
        )).doReturn(newToken.toSingle())

        val single = useCase.invoke()

        verify(getAppId).build()
        verify(getAppSecret).build()
        inOrder(accessTokenRepository, tokenEncryptor) {
            verify(accessTokenRepository).getRefreshToken()
            verify(accessTokenRepository).getRefreshedAccessToken(
                eq(appId),
                eq(appSecret),
                anyString(),
                anyString(),
                eq(refreshToken)
            )
            verify(tokenEncryptor).encrypt(newToken)
            verify(accessTokenRepository).saveToken(encryptedNewToken)
        }

        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
        }
    }

}
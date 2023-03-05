package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import com.kondenko.pocketwaka.utils.extensions.toSingle
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
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

    private val useCase = RefreshAccessToken(testSchedulers, dateProvider, tokenEncryptor, accessTokenRepository, getStoredAccessToken, getAppId, getAppSecret)

    lateinit var token: AccessToken

    @Before
    fun before() {
        token = AccessToken(
            "at",
            "rt",
            0.0,
            ZonedDateTime.now(),
            "s",
            "tt",
            "uid",
        )
    }

    @Test
    fun `token should be valid`() {
        TODO("Rewrite this test")
/*
        val token = token.copy(expiresIn = 100.0, createdAt = 1000f)
        assertTrue(token.isValid(1099f))
        assertTrue(token.isValid(1000f))
*/
    }

    @Test
    fun `token should NOT be valid`() {
        TODO("Rewrite this test")
/*
        val token = token.copy(expiresIn = 100.0, createdAt = 1000f)
        assertFalse(token.isValid(1100f))
        assertFalse(token.isValid(1101f))
*/
    }

    @Test
    fun `should return token if it's valid`() {
        val token: AccessToken = mock()
        val currentTime = 500f

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
        val currentTime = 500f
        val appId = "foo"
        val appSecret = "bar"
        val refreshToken = "baz"

        whenever(getStoredAccessToken.build()).doReturn(invalidToken.toSingle())
        whenever(dateProvider.getCurrentTimeSec()).doReturn(currentTime)
        whenever(invalidToken.isValid(anyFloat())).doReturn(false)
        whenever(getAppId.build()).doReturn(appId.toSingle())
        whenever(getAppSecret.build()).doReturn(appSecret.toSingle())
        whenever(accessTokenRepository.getRefreshToken()).doReturn(refreshToken.toSingle())
        whenever(tokenEncryptor.encrypt(newToken)).doReturn(encryptedNewToken)
        whenever(accessTokenRepository.getRefreshedAccessToken(eq(appId), eq(appSecret), anyString(), anyString(), eq(refreshToken))).doReturn(newToken.toSingle())

        val single = useCase.invoke()

        verify(getAppId).build()
        verify(getAppSecret).build()
        inOrder(accessTokenRepository, tokenEncryptor) {
            verify(accessTokenRepository).getRefreshToken()
            verify(accessTokenRepository).getRefreshedAccessToken(eq(appId), eq(appSecret), anyString(), anyString(), eq(refreshToken))
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
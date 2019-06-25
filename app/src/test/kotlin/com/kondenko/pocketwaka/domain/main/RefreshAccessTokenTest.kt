package com.kondenko.pocketwaka.domain.main

import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.repository.AccessTokenRepository
import com.kondenko.pocketwaka.domain.auth.GetAppId
import com.kondenko.pocketwaka.domain.auth.GetAppSecret
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.Encryptor
import com.kondenko.pocketwaka.utils.TimeProvider
import com.nhaarman.mockito_kotlin.*
import io.reactivex.rxkotlin.toSingle
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RefreshAccessTokenTest {

    private val timeProvider: TimeProvider = mock()

    private val encryptor: Encryptor = mock()

    private val accessTokenRepository: AccessTokenRepository = mock()

    private val getStoredAccessToken: GetStoredAccessToken = mock()

    private val getAppId: GetAppId = mock()

    private val getAppSecret: GetAppSecret = mock()

    private val useCase = RefreshAccessToken(testSchedulers, timeProvider, encryptor, accessTokenRepository, getStoredAccessToken, getAppId, getAppSecret)

    lateinit var token: AccessToken

    @Before
    fun before() {
        token = AccessToken("at", 0.0, "rt", "s", "tt", "uid", 0f)
    }

    @Test
    fun `token should be valid`() {
        val token = token.copy(expiresIn = 100.0, createdAt = 1000f)
        assertTrue(token.isValid(1099f))
        assertTrue(token.isValid(1000f))
    }

    @Test
    fun `token should NOT be valid`() {
        val token = token.copy(expiresIn = 100.0, createdAt = 1000f)
        assertFalse(token.isValid(1100f))
        assertFalse(token.isValid(1101f))
    }

    @Test
    fun `should return token if it's valid`() {
        val token: AccessToken = mock()
        val currentTime = 500f

        whenever(getStoredAccessToken.build()).doReturn(token.toSingle())
        whenever(timeProvider.getCurrentTimeSec()).doReturn(currentTime)
        whenever(token.isValid(currentTime)).doReturn(true)

        val single = useCase.invoke()
        verifyNoMoreInteractions(getAppId)
        verifyNoMoreInteractions(getAppSecret)
        verifyNoMoreInteractions(accessTokenRepository)
        verifyNoMoreInteractions(encryptor)
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
        whenever(timeProvider.getCurrentTimeSec()).doReturn(currentTime)
        whenever(invalidToken.isValid(anyFloat())).doReturn(false)
        whenever(getAppId.build()).doReturn(appId.toSingle())
        whenever(getAppSecret.build()).doReturn(appSecret.toSingle())
        whenever(accessTokenRepository.getRefreshToken()).doReturn(refreshToken.toSingle())
        whenever(encryptor.encryptToken(newToken)).doReturn(encryptedNewToken)
        whenever(accessTokenRepository.getRefreshedAccessToken(eq(appId), eq(appSecret), anyString(), anyString(), eq(refreshToken))).doReturn(newToken.toSingle())

        val single = useCase.invoke()

        verify(getAppId).build()
        verify(getAppSecret).build()
        inOrder(accessTokenRepository, encryptor) {
            verify(accessTokenRepository).getRefreshToken()
            verify(accessTokenRepository).getRefreshedAccessToken(eq(appId), eq(appSecret), anyString(), anyString(), eq(refreshToken))
            verify(encryptor).encryptToken(newToken)
            verify(accessTokenRepository).saveToken(encryptedNewToken, currentTime)
        }

        with(single.test()) {
            assertSubscribed()
            assertNoErrors()
            assertComplete()
        }
    }

}
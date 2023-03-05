package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.getAccessTokenMock
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import com.nhaarman.mockito_kotlin.*
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

class AccessTokenRepositoryTest {

    @get:Rule
    val rxRule = RxRule()

    private val accessTokenService: AccessTokenService = mock()

    private val sharedPrefs: SharedPreferences = mock()

    private val gson: Gson = mock()

    private val accessTokenRepository = AccessTokenRepository(accessTokenService, gson, sharedPrefs)

    @Test
    fun `should acquire new token`() {
        val token: AccessToken = mock()
        TODO("Rewrite this test taking JSON and HTML parsing into account")
        // whenever(accessTokenService.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString())).doReturn(Single.just(token))
        val tokenSingle = accessTokenRepository.getNewAccessToken("string", "string", "string", "string", "string")
        with(tokenSingle.test()) {
            assertNoErrors()
            assertComplete()
            assertValue(token)
        }
    }

    @Test
    fun `should fail if not saved`() {
        whenever(sharedPrefs.contains(anyString())).doReturn(false)
        val errorSingle = accessTokenRepository.getEncryptedToken()
        with(errorSingle.test()) {
            assertFailure(UnauthorizedException::class.java)
            assertNotComplete()
            assertTerminated()
        }
    }

    @Test
    fun `should return encrypted token`() {
        val tokenStringField = "foo"
        val expiresAtEncoded = "2023-05-04T10%3A59%3A47Z"
        val expiresAt = ZonedDateTime.of(
            2023, 5, 4, 10, 59, 47, 0, ZoneId.of("Z")
        )
        val token: AccessToken = getAccessTokenMock(tokenStringField, expiresAt)
        whenever(sharedPrefs.contains(anyString())).doReturn(true)
        whenever(sharedPrefs.getString(anyString(), anyOrNull())).doReturn(tokenStringField)
        whenever(sharedPrefs.getString(eq(KEY_EXPIRES_AT), anyOrNull())).doReturn(expiresAtEncoded)
        whenever(sharedPrefs.getFloat(anyString(), anyFloat())).doReturn(0f)
        val tokenSingle = accessTokenRepository.getEncryptedToken()
        with(tokenSingle.test()) {
            assertValue(token)
            assertNoErrors()
            assertComplete()
        }
    }

}
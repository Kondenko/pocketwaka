package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.getAccessTokenMock
import com.kondenko.pocketwaka.utils.exceptions.UnauthorizedException
import com.kondenko.pocketwaka.utils.extensions.toSingle
import com.nhaarman.mockito_kotlin.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
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
    fun `should acquire new token and parse HTML`() {
        val htmlResponse =
            "access_token=ACCESS_TOKEN" +
                    "&refresh_token=REFRESH_TOKEN" +
                    "&uid=UID" +
                    "&token_type=bearer" +
                    "&expires_at=2023-05-04T10%3A59%3A47Z" +
                    "&expires_in=5184000" +
                    "&scope=email%2Cread_stats%2Cread_logged_time"
        val responseBody = htmlResponse.toResponseBody("text/html".toMediaType())
        val expectedResult = AccessToken(
            accessToken = "ACCESS_TOKEN",
            refreshToken = "REFRESH_TOKEN",
            expiresIn = 5184000.0,
            expiresAt = ZonedDateTime.of(
                2023, 5, 4, 10, 59, 47, 0, ZoneId.of("Z")
            ),
            scope = "email,read_stats,read_logged_time",
            tokenType = "bearer",
            uid = "UID"
        )
        whenever(
            accessTokenService.getAccessToken(
                anyString(),
                anyString(),
                anyString(),
                anyString(),
                anyString(),
            )
        ) doReturn responseBody.toSingle()
        val tokenSingle = accessTokenRepository.getNewAccessToken(
            "id",
            "secret",
            "redirectUri",
            "grantType",
            "code"
        )
        with(tokenSingle.test()) {
            assertNoErrors()
            assertComplete()
            assertValue(expectedResult)
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
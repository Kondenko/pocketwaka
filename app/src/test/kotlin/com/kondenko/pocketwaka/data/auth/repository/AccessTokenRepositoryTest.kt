package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import com.kondenko.pocketwaka.data.auth.model.server.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.getAccessTokenMock
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyFloat
import org.mockito.ArgumentMatchers.anyString

class AccessTokenRepositoryTest {

    @get:Rule
    val rxRule = RxRule()

    private val accessTokenService: AccessTokenService = mock()

    private val sp: SharedPreferences = mock()

    private val accessTokenRepository = AccessTokenRepository(accessTokenService, sp)

    @Test
    fun `should acquire new token`() {
        val token: AccessToken = mock()
        whenever(accessTokenService.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .doReturn(Single.just(token))
        val tokenSingle = accessTokenRepository.getNewAccessToken("string", "string", "string", "string", "string")
        with(tokenSingle.test()) {
            assertNoErrors()
            assertComplete()
            assertValue(token)
        }
    }

    @Test
    fun `should fail if not saved`() {
        whenever(sp.contains(anyString())).doReturn(false)
        val errorSingle = accessTokenRepository.getEncryptedToken()
        with(errorSingle.test()) {
            assertFailure(NullPointerException::class.java)
            assertNotComplete()
            assertTerminated()
        }
    }

    @Test
    fun `should return encrypted token`() {
        val tokenStringField = "foo"
        val token: AccessToken = getAccessTokenMock(tokenStringField)
        whenever(sp.contains(anyString())).doReturn(true)
        whenever(sp.getString(anyString(), anyOrNull())).doReturn(tokenStringField)
        whenever(sp.getFloat(anyString(), anyFloat())).doReturn(0f)
        val tokenSingle = accessTokenRepository.getEncryptedToken()
        with(tokenSingle.test()) {
            assertValue(token)
            assertNoErrors()
            assertComplete()
        }
    }

}
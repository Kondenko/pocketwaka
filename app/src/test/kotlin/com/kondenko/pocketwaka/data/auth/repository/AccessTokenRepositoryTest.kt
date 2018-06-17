package com.kondenko.pocketwaka.data.auth.repository

import android.content.SharedPreferences
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AccessTokenService
import com.kondenko.pocketwaka.testutils.RxRule
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString

class AccessTokenRepositoryTest {

    @get:Rule
    val rxRule = RxRule()

    private val accessTokenService: AccessTokenService = mock()

    private val sp: SharedPreferences = mock()

    private val accessTokenRepository = AccessTokenRepository(accessTokenService, sp)

    private val token: AccessToken = mock()

    @Test
    fun `should acquire new token`() {
        whenever(accessTokenService.getAccessToken(anyString(), anyString(), anyString(), anyString(), anyString()))
                .doReturn(Single.just(mock()))
        val tokenSingle = accessTokenService.getAccessToken("string", "string", "string", "string", "string")
        tokenSingle.test().assertNoErrors()
        tokenSingle.test().assertComplete()
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

    // For some reason passing a null as a default parameter causes an NPE
    @Test(expected = NullPointerException::class)
    fun `should return encrypted token`() {
        whenever(sp.contains(anyString())).doReturn(true)
        whenever(sp.getString(anyString(), anyOrNull())).doReturn("foo")
        whenever(sp.getFloat(anyString(), anyOrNull())).doReturn(0f)
        val tokenSingle = accessTokenRepository.getEncryptedToken()
        with(tokenSingle.test()) {
            assertNoErrors()
            assertComplete()
            assertResult(token)
        }
    }

}
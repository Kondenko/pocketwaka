package com.kondenko.pocketwaka.screens.activities.main

import com.kondenko.pocketwaka.RxSchedulersOverrideRule
import com.kondenko.pocketwaka.TestApp
import com.kondenko.pocketwaka.api.oauth.AccessToken
import com.kondenko.pocketwaka.api.oauth.InvalidAccessTokenTest
import com.kondenko.pocketwaka.api.oauth.ValidAccessTokenTest
import com.kondenko.pocketwaka.api.services.TokenService
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = "src/main/AndroidManifest.xml", application = TestApp::class)
class MainActivityPresenterTest {

    @Rule
    @JvmField
    val rxRule = RxSchedulersOverrideRule()

    //    @Inject
    val tokenService: TokenService = mock()
    val view: MainActivityView = mock()

    lateinit var presenter: MainActivityPresenter

    @Before
    fun setup() {
        presenter = MainActivityPresenter(tokenService, view)
    }

    @Test
    fun testInvalidToken() {
        // When a token is invalid, presenter should fetch a new one
        val invalidToken = InvalidAccessTokenTest()
        val validToken = ValidAccessTokenTest()
        presenter.updateToken(invalidToken, "", "", "")
        doReturn(Single.just(validToken)).whenever(tokenService.getRefreshToken(anyString(), anyString(), anyString(), anyString(), anyString()))
        verify(view).onTokenRefreshSuccess(any<AccessToken>())
    }

}
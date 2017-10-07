package com.kondenko.pocketwaka.screens.main

import com.kondenko.pocketwaka.RxSchedulersOverrideRule
import com.kondenko.pocketwaka.TestApp
import com.kondenko.pocketwaka.api.oauth.InvalidAccessTokenTest
import com.kondenko.pocketwaka.api.oauth.ValidAccessTokenTest
import com.kondenko.pocketwaka.data.auth.model.AccessToken
import com.kondenko.pocketwaka.data.auth.service.AuthService
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
class MainActivityActivityPresenterTest {

    @Rule
    @JvmField
    val rxRule = RxSchedulersOverrideRule()

    //    @Inject
    val authService: AuthService = mock()
    val view: MainView = mock()

    lateinit var presenter: MainActivityPresenter

    @Before
    fun setup() {
        presenter = MainActivityPresenter(authService)
    }

    @Test
    fun testInvalidToken() {
        // When a token is invalid, presenter should fetch a new one
        val invalidToken = InvalidAccessTokenTest()
        val validToken = ValidAccessTokenTest()
        presenter.updateToken(invalidToken, "", "", "")
        doReturn(Single.just(validToken)).whenever(authService.getRefreshToken(anyString(), anyString(), anyString(), anyString(), anyString()))
        verify(view).onTokenRefreshSuccess(any<AccessToken>())
    }

}
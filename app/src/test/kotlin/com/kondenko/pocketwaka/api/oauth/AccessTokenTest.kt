package com.kondenko.pocketwaka.api.oauth

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest=Config.NONE)
class AccessTokenTest {

    @Test
    fun testValidToken() {
        val validToken = ValidAccessTokenTest()
        assertTrue(validToken.isValid())
    }

    @Test
    fun testInvalidToken() {
        val invalidToken = InvalidAccessTokenTest()
        assertFalse(invalidToken.isValid())
    }

}
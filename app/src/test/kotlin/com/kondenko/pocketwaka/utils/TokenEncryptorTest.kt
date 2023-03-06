package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.testutils.getAccessTokenMock
import com.kondenko.pocketwaka.utils.encryption.Encryptor
import com.kondenko.pocketwaka.utils.encryption.TokenEncryptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.threeten.bp.ZonedDateTime

class TokenEncryptorTest {

    private val stringEncryptor: Encryptor<String> = mock()

    private val encryptor = TokenEncryptor(stringEncryptor)

    private val decryptedString = "decrypted"

    private val token = getAccessTokenMock(decryptedString, ZonedDateTime.now())

    @Before
    fun setup() {
        whenever(stringEncryptor.decrypt(anyString())).doReturn(decryptedString)
        whenever(stringEncryptor.encrypt(anyString())).doReturn("encrypted")
    }

    @Test
    fun shouldEncryptCorrectly() {
        val encryptedToken = encryptor.encrypt(token)
        assertNotEquals(token, encryptedToken)
    }

    @Test
    fun shouldDecryptCorrectly() {
        val encryptedToken = encryptor.encrypt(token)
        val decryptedToken = encryptor.decrypt(encryptedToken)
        assertNotEquals(encryptedToken, decryptedToken)
        assertEquals(token, decryptedToken)
    }

}
package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.utils.extensions.times
import org.junit.Assert.assertEquals
import org.junit.Test

class FunctionsTest {

    @Test
    fun times() {
        val list = listOf(1, 2, 3)
        val expected = listOf(1, 2, 3, 1, 2, 3, 1, 2, 3)
        val actual = list * 3
        assertEquals(expected, actual)
    }

}
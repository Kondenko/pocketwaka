package com.kondenko.pocketwaka.utils.extensions

import org.junit.Assert.assertEquals
import org.junit.Test

class OtherKtTest {

    @Test
    fun appendOrReplace1() {
        data class Model(val id: Int, val iteration: Int)
        val first = listOf(Model(0, 0), Model(1, 0), Model(2, 0))
        val second = listOf(Unit, Model(1, 1), Model(2, 0))
        val actual = first.appendOrReplace(second) { if (it is Model) it.id else Unit }
        val expected = listOf(Model(0, 0), Unit, Model(1, 1), Model(2, 0))
        assertEquals(expected, actual)
    }

    @Test
    fun appendOrReplace2() {
        data class Model(val id: Int, val iteration: Int)
        val first = listOf(Unit, Model(0, 0), Model(1, 0), Model(2, 0))
        val second = listOf(Unit, Model(0, 1), Model(1, 1), Model(2, 1))
        val actual = first.appendOrReplace(second) { if (it is Model) it.id else it }
        val expected = listOf(Unit, Model(0, 1), Model(1, 1), Model(2, 1))
        assertEquals(expected, actual)
    }

    @Test
    fun appendOrReplace3() {
        data class Model(val id: Int, val iteration: Int)
        val first = listOf(Unit, Model(0, 0), Model(1, 0))
        val second = listOf(Model(0, 1), Model(1, 0))
        val actual = first.appendOrReplace(second) { if (it is Model) it.id else it }
        val expected = listOf(Unit, Model(0, 1), Model(1, 0))
        assertEquals(expected, actual)
    }

}
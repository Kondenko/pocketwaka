package com.kondenko.pocketwaka.utils.extensions

import org.junit.Assert.assertEquals
import org.junit.Test
import org.threeten.bp.LocalDate

class DailyRangeTest {

    @Test(timeout = 1000)
    fun `should contain 1 day`() {
        val start = LocalDate.of(2020, 1, 1)
        val end = LocalDate.of(2020, 1, 1)
        assertEquals(listOf(start), start dailyRangeTo end)
    }

    @Test(timeout = 1000)
    fun `should contain 3 days`() {
        val start = LocalDate.of(2020, 1, 1)
        val end = LocalDate.of(2020, 1, 3)
        assertEquals(
              listOf(
                    start,
                    LocalDate.of(2020, 1, 2),
                    end
              ),
              start dailyRangeTo end
        )
    }

    @Test(expected = IllegalArgumentException::class, timeout = 1000)
    fun `should throw an exception when end date comes before start date`() {
        LocalDate.of(2020, 1, 31) dailyRangeTo LocalDate.of(2020, 1, 1)
    }

}
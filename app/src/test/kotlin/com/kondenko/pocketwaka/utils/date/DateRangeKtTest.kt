package com.kondenko.pocketwaka.utils.date

import com.kondenko.pocketwaka.TestApp
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.threeten.bp.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21], application = TestApp::class)
class DateRangeKtTest {

    @Test
    fun `single day should contain the date`() {
        val date = LocalDate.now()
        val range = DateRange.SingleDay(date)
        assertTrue(date in range)
    }

    @Test
    fun `range should contain the date`() {
        val end = LocalDate.now()
        val start = end.minusDays(5)
        val date = end.minusDays(1)
        val range = DateRange.Range(start, end)
        assertTrue(date in range)
    }

    @Test
    fun `range should be inclusive`() {
        val end = LocalDate.now()
        val start = end.minusDays(5)
        val range = DateRange.Range(start, end)
        assertTrue(start in range)
        assertTrue(end in range)
    }

    @Test
    fun `range should NOT contain the date`() {
        val end = LocalDate.now()
        val start = end.minusDays(5)
        val date = end.minusDays(10)
        val range = DateRange.Range(start, end)
        assertFalse(date in range)
    }

    @Test
    fun `single day should NOT contain the date`() {
        val date = LocalDate.now()
        val dateUnderTest = LocalDate.now().minusDays(1)
        val range = DateRange.SingleDay(date)
        assertFalse(dateUnderTest in range)
    }

}
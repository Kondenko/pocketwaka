package com.kondenko.pocketwaka.utils.date

import com.kondenko.pocketwaka.App
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.ClosingKoinTest
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.threeten.bp.LocalDate

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [22], application = App::class)
class DateRangeKtTest : ClosingKoinTest {

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

    @Test
    fun `iterable should include all days`() {
        val start = LocalDate.of(2020, 12, 1)
        val end = LocalDate.of(2020, 12, 5)
        val range = DateRange.Range(start, end)
        val days = range.toListOfDays()
        assertEquals(
              listOf(
                    LocalDate.of(2020, 12, 1),
                    LocalDate.of(2020, 12, 2),
                    LocalDate.of(2020, 12, 3),
                    LocalDate.of(2020, 12, 4),
                    LocalDate.of(2020, 12, 5),
              ),
              days
        )
    }

    @Test
    fun `iterable should be empty for an invalid range`() {
        val start = LocalDate.of(2020, 12, 5)
        val end = LocalDate.of(2020, 12, 1)
        val range = DateRange.Range(start, end)
        val days = range.toListOfDays()
        assertTrue(days.isEmpty())
    }

}
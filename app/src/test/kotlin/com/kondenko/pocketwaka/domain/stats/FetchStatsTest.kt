package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.rxkotlin.toSingle
import org.junit.Test

class FetchStatsTest {

    private val getTokenHeader: GetTokenHeaderValue = mock()

    private val statsRepository: StatsRepository = mock()

    private val useCase = FetchStats(testSchedulers, getTokenHeader, statsRepository)

    @Test
    fun `should fetch token first`() {
        val header = "foo"
        val range = "bar"
        whenever(getTokenHeader.build()).doReturn(header.toSingle())
        useCase.execute(range)
        val order = inOrder(getTokenHeader, statsRepository)
        order.verify(getTokenHeader).build()
        order.verify(statsRepository).getStats(header, range)
    }

}
package com.kondenko.pocketwaka.domain.ranges

import com.kondenko.pocketwaka.data.ranges.repository.StatsRepository
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.ranges.usecase.FetchStats
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

    private val header = "foo"

    private val range = "bar"

    @Test
    fun `should fetch token first`() {
        whenever(getTokenHeader.build()).doReturn(header.toSingle())
        useCase.invoke(FetchStats.Params(range))
        inOrder(getTokenHeader, statsRepository) {
            verify(getTokenHeader).build()
            verify(statsRepository).getData(StatsRepository.Params(header, range))
        }
    }

}

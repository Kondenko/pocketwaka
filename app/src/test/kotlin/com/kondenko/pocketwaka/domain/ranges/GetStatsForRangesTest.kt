package com.kondenko.pocketwaka.domain.ranges

import com.kondenko.pocketwaka.data.ranges.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.ranges.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.ranges.repository.RangeStatsRepository
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import io.reactivex.rxkotlin.toSingle
import org.junit.Test
import timber.log.Timber


class GetStatsForRangesTest {

    private val getTokenHeader: GetTokenHeaderValue = mock()

    private val rangeStatsRepository: RangeStatsRepository = mock()

    private val dbModel: StatsDbModel = mock()

    private val converter: (RangeStatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel> = mock()

    private val useCase = GetStatsForRanges(testSchedulers, getTokenHeader, rangeStatsRepository, converter)

    private val header = "foo"

    private val range = "bar"

    @Test
    fun `should fetch token first`() {
        whenever(getTokenHeader.build()).doReturn(header.toSingle())
        whenever(converter.invoke(any(), any())).doReturn(Maybe.just(dbModel).also { Timber.d(it.toString()) })
        useCase.invoke(GetStatsForRanges.Params(range))
        inOrder(getTokenHeader, converter, rangeStatsRepository) {
            verify(getTokenHeader).build()
            verify(rangeStatsRepository).getData(eq(RangeStatsRepository.Params(header, range)), any())
        }
    }

}

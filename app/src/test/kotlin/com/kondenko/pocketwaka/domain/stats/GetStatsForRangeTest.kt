package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.stats.model.database.StatsDbModel
import com.kondenko.pocketwaka.data.stats.model.server.StatsServerModel
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.stats.usecase.GetStatsForRange
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.extensions.toSingle
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Maybe
import org.junit.Test
import timber.log.Timber


class GetStatsForRangeTest {

    private val getTokenHeader: GetTokenHeaderValue = mock()

    private val statsRepository: StatsRepository = mock()

    private val dbModel: StatsDbModel = mock()

    private val converter: (StatsRepository.Params, StatsServerModel) -> Maybe<StatsDbModel> = mock()

    private val useCase = GetStatsForRange(testSchedulers, getTokenHeader, statsRepository, converter)

    private val header = "foo"

    private val range = "bar"

    @Test
    fun `should fetch token first`() {
        whenever(getTokenHeader.build()).doReturn(header.toSingle())
        whenever(converter.invoke(any(), any())).doReturn(Maybe.just(dbModel).also { Timber.d(it.toString()) })
        useCase.invoke(GetStatsForRange.Params(range))
        inOrder(getTokenHeader, converter, statsRepository) {
            verify(getTokenHeader).build()
            verify(statsRepository).getData(eq(StatsRepository.Params(header, range)), any())
        }
    }

}

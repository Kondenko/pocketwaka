package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.nhaarman.mockito_kotlin.*
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import java.util.concurrent.TimeUnit

class GetStatsStateTest {

    private val fetchStats: FetchStats = mock()

    private val getSkeletonPlaceholderData: GetSkeletonPlaceholderData = mock()

    private val testScheduler = TestScheduler()

    private val useCase = GetStatsState(SchedulersContainer(testScheduler, testScheduler), getSkeletonPlaceholderData, fetchStats)

    @Test
    fun build() {
        val refreshInterval = 1
        val params = GetStatsState.Params("foo", refreshInterval)
        val skeletonModel: StatsModel = mock()
        val actualModel: StatsModel = mock()

        whenever(getSkeletonPlaceholderData.build()).doReturn(skeletonModel.toSingle())
        whenever(fetchStats.build(params.range)).doReturn(actualModel.toSingle())

        val observable = useCase.execute(params)
        val testObserver = observable.test()

        testScheduler.triggerActions()

        inOrder(getSkeletonPlaceholderData, fetchStats) {
            verify(getSkeletonPlaceholderData).build()
            verify(fetchStats, times(2)).build(params.range)
            verifyNoMoreInteractions()
        }

        testObserver.assertValuesOnly(State.Loading(skeletonModel), State.Success(actualModel))

        testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)

        with(testObserver) {
            assertValuesOnly(State.Loading(skeletonModel), State.Success(actualModel), State.Success(actualModel))
        }

        with(testObserver) {
            assertNoErrors()
            dispose()
        }
    }

}
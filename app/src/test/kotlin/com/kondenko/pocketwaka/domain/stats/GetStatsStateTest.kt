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
    fun `should show loading first and then update data`() {
        val refreshInterval = 1
        val params = GetStatsState.Params("foo", refreshInterval)
        val skeletonModel: List<StatsModel> = mock()
        val actualModel: List<StatsModel> = mock()

        whenever(getSkeletonPlaceholderData.build()).doReturn(skeletonModel.toSingle())
        whenever(fetchStats.build(params.range)).doReturn(actualModel.toSingle())

        val observable = useCase.execute(params)
        val testObserver = observable.test()

        inOrder(getSkeletonPlaceholderData, fetchStats) {
            testScheduler.triggerActions()
            verify(getSkeletonPlaceholderData).build()
            verify(fetchStats, times(2)).build(params.range)
            verifyNoMoreInteractions()
        }

        with(testObserver) {
            assertValueAt(0) { it is State.Loading && it.skeletonData === skeletonModel }
            assertValueAt(1) { it is State.Success && it.data === actualModel }
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Success && it.data === actualModel }
            assertValueCount(3)
            assertNoErrors()
            dispose()
        }
    }

}

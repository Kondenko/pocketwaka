package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.ErrorType
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.testWithLogging
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.inOrder
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class GetStatsStateTest {

    @get:Rule
    val rxRule = RxRule()

    private val testScheduler = TestScheduler()

    private val getSkeletonPlaceholderData: GetSkeletonPlaceholderData = mock()

    private val fetchStats: FetchStats = mock()

    private val connectivityStatusProvider: ConnectivityStatusProvider = mock()

    private val getState = GetStatsState(
            SchedulersContainer(testScheduler, testScheduler),
            getSkeletonPlaceholderData,
            fetchStats,
            connectivityStatusProvider
    )

    private val refreshInterval = 1

    private val params = GetStatsState.Params("foo", refreshInterval)

    private val skeletonModel: List<StatsModel> = mock()

    private val actualModel: List<StatsModel> = mock()

    @Test
    fun `should show loading first and then update stats`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Single.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
        val testObserver = getState.execute(params).testWithLogging()
        inOrder(getSkeletonPlaceholderData, fetchStats, connectivityStatusProvider) {
            testScheduler.triggerActions()
            verify(connectivityStatusProvider).isNetworkAvailable()
//            verify(getSkeletonPlaceholderData).build()
            verify(fetchStats).build(params.range)
            verifyNoMoreInteractions()
        }
        with(testObserver) {
            assertValueAt(0) { it is State.Loading && it.skeletonData === skeletonModel }
            assertValueAt(1) { it is State.Success && it.data === actualModel }
            assertValueCount(2)
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should update stats every minute`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Single.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
        with(getState.execute(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading && it.skeletonData === skeletonModel }
            assertValueAt(1) { it is State.Success && it.data === actualModel }
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Success && it.data === actualModel }
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Success && it.data === actualModel }
            assertValueCount(4)
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an error state if no data and offline`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(false))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Single.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(Observable.error(TestException("No network")))
        with(getState.execute(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Failure && it.errorType is ErrorType.NoNetwork }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

}

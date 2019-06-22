package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.assertOneOfValues
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.validateMockitoUsage
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


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

    @After
    fun validate() {
        validateMockitoUsage()
    }

    @Test
    fun `should show loading first and then update stats`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
        val testObserver = getState.invoke(params).test()
        testScheduler.triggerActions()
        verify(connectivityStatusProvider).isNetworkAvailable()
        verify(getSkeletonPlaceholderData).build()
        verify(fetchStats, atLeastOnce()).build(params.range)
        with(testObserver) {
            assertValueAt(0) { it is State.Loading && it.skeletonData === skeletonModel && it.isInterrupting }
            assertValueAt(1) { it is State.Success && it.data === actualModel }
            assertValueCount(2)
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should update stats every minute`() {
        var model: List<StatsModel> = mock()
        val testStatsSubject = BehaviorSubject.createDefault(model)
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(testStatsSubject)
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading && it.skeletonData == skeletonModel }
            assertValueAt(1) { it is State.Success && it.data == model }
            testStatsSubject.onNext(model)
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Loading && !it.isInterrupting }
            assertValueAt(3) { it is State.Success && it.data == model }
            testStatsSubject.onNext(model)
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(4) { it is State.Loading && !it.isInterrupting }
            assertValueAt(5) { it is State.Success && it.data == model }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an error state if no data and offline`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(false))
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(fetchStats.build(params.range)).doReturn(Observable.error(TestException("No network")))
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            assertValue { it is State.Failure.NoNetwork<*> }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an offline state with cached data`() {
        val testConnectivitySubject = BehaviorSubject.createDefault<Boolean>(true)
        val testStatsSubject = BehaviorSubject.createDefault<List<StatsModel>>(actualModel)
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(testConnectivitySubject)
        whenever(fetchStats.build(params.range)).doReturn(testStatsSubject)
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            testConnectivitySubject.onNext(false)
            testStatsSubject.onError(TestException())
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Offline && it.data == actualModel }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an offline state and then update with new data`() {
        val testConnectivitySubject = BehaviorSubject.createDefault<Boolean>(true)
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(testConnectivitySubject)
        whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            testConnectivitySubject.onNext(false)
            whenever(fetchStats.build(params.range)).doReturn(Observable.error(TestException()))
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Offline && it.data == actualModel }
            whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
            testConnectivitySubject.onNext(true)
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(3) { it is State.Loading && !it.isInterrupting }
            assertValueAt(4) { it is State.Success }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should accept all errors while maintaining current state`() {
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(fetchStats.build(params.range)).doReturn(Observable.just(actualModel))
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success }
            TestException().let { exception ->
                whenever(fetchStats.build(params.range)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(2) { it is State.Loading && !it.isInterrupting }
                assertValueAt(3) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            TimeoutException().let { exception ->
                whenever(fetchStats.build(params.range)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(4) { it is State.Loading && !it.isInterrupting }
                assertValueAt(5) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            RuntimeException().let { exception ->
                whenever(fetchStats.build(params.range)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(6) { it is State.Loading && !it.isInterrupting }
                assertValueAt(7) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            TestException().let { exception ->
                whenever(fetchStats.build(params.range)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(8) { it is State.Loading && !it.isInterrupting }
                assertValueAt(9) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            assertNoErrors()
            assertNotTerminated()
            assertNotComplete()
            dispose()
        }
    }

    @Test
    fun `should show an error state after retrying several times if no data is present`() {
        whenever(getSkeletonPlaceholderData.build()).doReturn(Observable.just(skeletonModel))
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(fetchStats.build(params.range)).doReturn(Observable.error(TestException()))
        with(getState.invoke(params).test()) {
            testScheduler.triggerActions()
            verify(fetchStats, atLeast(2)).build(params.range)
            assertOneOfValues { it is State.Failure.Unknown<*> && it.isFatal && it.data == null }
            assertNoErrors()
            assertNotTerminated()
            assertNotComplete()
            dispose()
        }
    }

}

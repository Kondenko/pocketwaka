package com.kondenko.pocketwaka.domain.ranges

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.ranges.dto.StatsDto
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsForRanges
import com.kondenko.pocketwaka.domain.ranges.usecase.GetStatsState
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.extensions.testWithLogging
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

    private val getStatsForRanges: GetStatsForRanges = mock()

    private val connectivityStatusProvider: ConnectivityStatusProvider = mock()

    private val getState = GetStatsState(
            SchedulersContainer(testScheduler, testScheduler),
            getStatsForRanges,
            connectivityStatusProvider
    )

    private val refreshInterval = 1

    private val retryAttempts = 3

    private val range = "foo"

    private val params = GetStatsForRanges.Params(range, refreshInterval, retryAttempts)

    private val cachedModel: List<StatsUiModel> = listOf(
            StatsUiModel.Info("1h", "1h")
    )

    private val actualModel: List<StatsUiModel> = listOf(
            StatsUiModel.Info("1h", "1h")
    )

    private val cacheDto = StatsDto(range, 0, true, false, cachedModel)

    private val serverDto = StatsDto(range, 0, false, false, actualModel)

    @After
    fun validate() {
        validateMockitoUsage()
    }

    @Test
    fun `should show an empty state`() {
        val emptyDto = StatsDto(range, 0, false, true, actualModel)
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getStatsForRanges.build(params)).doReturn(Observable.just(emptyDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading && it.isInterrupting }
            assertValueAt(1) { it is State.Empty }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show loading first and then update stats`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getStatsForRanges.build(params)).doReturn(Observable.just(serverDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            verify(connectivityStatusProvider).isNetworkAvailable()
            verify(getStatsForRanges, atLeastOnce()).build(params)
            assertValueAt(0) { it is State.Loading && it.isInterrupting }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            assertValueCount(2)
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should update stats every minute`() {
        val testStatsSubject = BehaviorSubject.createDefault(serverDto)
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getStatsForRanges.build(params)).doReturn(testStatsSubject)
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading && it.isInterrupting }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            testStatsSubject.onNext(serverDto)
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Loading && !it.isInterrupting }
            assertValueAt(3) { it is State.Success && it.data == actualModel }
            testStatsSubject.onNext(serverDto)
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(4) { it is State.Loading && !it.isInterrupting }
            assertValueAt(5) { it is State.Success && it.data == actualModel }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an error state if no data and offline`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(false))
        whenever(getStatsForRanges.build(params)).doReturn(Observable.error(TestException("No network")))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Failure.NoNetwork<*> && it.isFatal }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an offline state with data`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(false))
        whenever(getStatsForRanges.build(params)).doReturn(Observable.just(cacheDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Offline && it.data != null && it.data == cachedModel }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an offline state with cached data`() {
        val testConnectivitySubject = BehaviorSubject.createDefault(true)
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(testConnectivitySubject)
        with(getState.invoke(params).testWithLogging()) {
            whenever(getStatsForRanges.build(params)).doReturn(Observable.just(serverDto))
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            testConnectivitySubject.onNext(false)
            whenever(getStatsForRanges.build(params)).doReturn(Observable.just(cacheDto))
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(2) { it is State.Loading && !it.isInterrupting }
            assertValueAt(3) { it is State.Offline && it.data == actualModel }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an offline state and then update with new data`() {
        val testConnectivitySubject = BehaviorSubject.createDefault(true)
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(testConnectivitySubject)
        whenever(getStatsForRanges.build(params)).doReturn(Observable.just(serverDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success && it.data == actualModel }
            testConnectivitySubject.onNext(false)
            whenever(getStatsForRanges.build(params)).doReturn(Observable.just(cacheDto))
            testScheduler.triggerActions()
            assertValueAt(2) { it is State.Loading && !it.isInterrupting }
            assertValueAt(3) { it is State.Offline && it.data == actualModel }
            testConnectivitySubject.onNext(true)
            whenever(getStatsForRanges.build(params)).doReturn(Observable.just(serverDto))
            testScheduler.triggerActions()
            assertValueAt(4) { it is State.Loading && !it.isInterrupting }
            assertValueAt(5) { it is State.Success }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should accept all errors while maintaining current state`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(true))
        whenever(getStatsForRanges.build(params)).doReturn(Observable.just(serverDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Success }
            TestException().let { exception ->
                whenever(getStatsForRanges.build(params)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(2) { it is State.Loading && !it.isInterrupting }
                assertValueAt(3) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            TimeoutException().let { exception ->
                whenever(getStatsForRanges.build(params)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(4) { it is State.Loading && !it.isInterrupting }
                assertValueAt(5) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            RuntimeException().let { exception ->
                whenever(getStatsForRanges.build(params)).doReturn(Observable.error(exception))
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assertValueAt(6) { it is State.Loading && !it.isInterrupting }
                assertValueAt(7) { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
            }
            TestException().let { exception ->
                whenever(getStatsForRanges.build(params)).doReturn(Observable.error(exception))
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

}

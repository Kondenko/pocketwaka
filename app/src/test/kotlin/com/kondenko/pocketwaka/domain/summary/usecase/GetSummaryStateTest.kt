package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.main.ClearCache
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.summary.SummaryState
import com.kondenko.pocketwaka.testutils.RxRule
import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.assertInOrder
import com.kondenko.pocketwaka.utils.extensions.testWithLogging
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.BehaviorSubject
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class GetSummaryStateTest {

    private val actualModel: List<SummaryUiModel> = listOf(
          SummaryUiModel.TimeTracked("2h", 100)
    )

    val firstPage = actualModel

    val secondPage = actualModel + listOf(SummaryUiModel.ProjectsTitle)

    private val serverDto = SummaryDbModel(1L, false, false, false, actualModel)

    private val refreshInterval = 1

    private val params = GetSummary.Params(
          dateRange = DateRange.PredefinedRange.ThisWeek.range,
          project = "project",
          branches = "master",
          refreshRate = refreshInterval,
          retryAttempts = 3
    )

    @get:Rule
    val rxRule = RxRule()

    private val testScheduler = TestScheduler()

    private val connectivityStatusProvider: ConnectivityStatusProvider = mock {
        on { it.isNetworkAvailable() }.thenReturn(Observable.just(true))
    }

    private val dateProvider: DateProvider = mock()

    private val getSummary: GetSummary = mock {
        on { it.build() }.thenReturn(Observable.fromArray(serverDto.copy(data = firstPage), serverDto.copy(data = secondPage)))
    }

    private val clearCache: ClearCache = mock()

    private val shouldShowOnboarding: ShouldShowOnboarding = mock {
        on { it.invoke() }.thenReturn(false)
    }

    private val getState = GetSummaryState(
          getSummary = getSummary,
          clearCache = clearCache,
          shouldShowOnboarding = shouldShowOnboarding,
          connectivityStatusProvider = connectivityStatusProvider,
          dateProvider = dateProvider,
          schedulers = SchedulersContainer(testScheduler, testScheduler),
    )

    @After
    fun validate() {
        Mockito.validateMockitoUsage()
    }

    @Test
    fun `should show an empty state for the given range`() {
        val emptyDto = serverDto.copy(isEmpty = true)
        whenever(getSummary.build(params)).doReturn(Observable.just(emptyDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading && it.isInterrupting }
            assertValueAt(1) { it is SummaryState.EmptyRange }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show loading first and then update stats`() {
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            verify(connectivityStatusProvider).isNetworkAvailable()
            verify(getSummary, atLeastOnce()).build(params)
            assertInOrder {
                assert { it is State.Loading && it.isInterrupting }
                assert { it is State.Loading && !it.isInterrupting && it.data == firstPage }
                assert { it is State.Loading && !it.isInterrupting && it.data == secondPage }
                assert { it is State.Success && it.data == secondPage }
            }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should update stats every minute`() {
        val testStatsSubject = BehaviorSubject.createDefault(serverDto)
        whenever(getSummary.build(params)).doReturn(testStatsSubject)
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertInOrder {
                assert { it is State.Loading && it.isInterrupting }
                assert { it is State.Success && it.data == actualModel }
                testStatsSubject.onNext(serverDto)
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Success && it.data == actualModel }
                testStatsSubject.onNext(serverDto)
                testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Success && it.data == actualModel }
            }
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should show an error state if no data and offline`() {
        whenever(connectivityStatusProvider.isNetworkAvailable()).doReturn(Observable.just(false))
        whenever(getSummary.build(params)).doReturn(Observable.error(TestException("No network")))
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
        whenever(getSummary.build(params)).doReturn(Observable.just(serverDto.copy(isFromCache = true)))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Offline && it.data != null }
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
            whenever(getSummary.build(params)).doReturn(Observable.just(serverDto))
            testScheduler.triggerActions()
            assertValueAt(0) { it is State.Loading }
            assertValueAt(1) { it is State.Loading }
            assertValueAt(2) { it is State.Success && it.data == actualModel }
            testConnectivitySubject.onNext(false)
            whenever(getSummary.build(params)).doReturn(Observable.just(serverDto.copy(isFromCache = true)))
            testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
            assertValueAt(3) { it is State.Loading && !it.isInterrupting }
            assertValueAt(4) { it is State.Loading && !it.isInterrupting }
            assertValueAt(5) { it is State.Offline && it.data == actualModel }
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
        whenever(getSummary.build(params)).doReturn(Observable.just(serverDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertInOrder {
                assert { it is State.Loading }
                assert { it is State.Loading }
                assert { it is State.Success && it.data == actualModel }
                testConnectivitySubject.onNext(false)
                whenever(getSummary.build(params)).doReturn(Observable.just(serverDto.copy(isFromCache = true)))
                testScheduler.triggerActions()
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Offline && it.data == actualModel }
                testConnectivitySubject.onNext(true)
                whenever(getSummary.build(params)).doReturn(Observable.just(serverDto))
                testScheduler.triggerActions()
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Loading && !it.isInterrupting }
                assert { it is State.Success }
            }
            assertNotTerminated()
            assertNotComplete()
            assertNoErrors()
            dispose()
        }
    }

    @Test
    fun `should accept all errors while maintaining current state`() {
        whenever(getSummary.build(params)).doReturn(Observable.just(serverDto))
        with(getState.invoke(params).testWithLogging()) {
            testScheduler.triggerActions()
            assertInOrder {
                assert { it is State.Loading }
                assert { it is State.Success }
                TestException().let { exception ->
                    whenever(getSummary.build(params)).doReturn(Observable.error(exception))
                    testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                    assert { it is State.Loading && !it.isInterrupting }
                    assert { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
                }
                TimeoutException().let { exception ->
                    whenever(getSummary.build(params)).doReturn(Observable.error(exception))
                    testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                    assert { it is State.Loading && !it.isInterrupting }
                    assert { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
                }
                RuntimeException().let { exception ->
                    whenever(getSummary.build(params)).doReturn(Observable.error(exception))
                    testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                    assert { it is State.Loading && !it.isInterrupting }
                    assert { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
                }
                TestException().let { exception ->
                    whenever(getSummary.build(params)).doReturn(Observable.error(exception))
                    testScheduler.advanceTimeBy(refreshInterval.toLong(), TimeUnit.MINUTES)
                    assert { it is State.Loading && !it.isInterrupting }
                    assert { it is State.Failure.Unknown<*> && it.data == actualModel && it.exception == exception }
                }
            }
            assertNoErrors()
            assertNotTerminated()
            assertNotComplete()
            dispose()
        }
    }

}

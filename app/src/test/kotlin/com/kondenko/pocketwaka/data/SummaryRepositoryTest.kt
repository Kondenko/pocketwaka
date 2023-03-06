package com.kondenko.pocketwaka.data

import com.kondenko.pocketwaka.data.summary.dao.SummaryDao
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.data.summary.service.SummaryService
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.testutils.testSchedulers
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRangeString
import com.kondenko.pocketwaka.utils.extensions.assertInOrder
import com.kondenko.pocketwaka.utils.extensions.testWithLogging
import com.nhaarman.mockito_kotlin.anyOrNull
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.TestScheduler
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import java.util.concurrent.TimeUnit

class SummaryRepositoryTest {

    private val scheduler = TestScheduler()

    private val service: SummaryService = mock()
    private val dao: SummaryDao = mock()
    private val repository = SummaryRepository(service, dao, testSchedulers.workerScheduler)
    { _, _, _ -> dbModel }

    private val uiModels = listOf(SummaryUiModel.TimeTracked("", 0))

    private val dbModel = SummaryDbModel(0, false, false, false, uiModels)

    private val dataFromServer = Summary(emptyList())

    private val dataFromCache = listOf(dbModel.copy(isEmpty = true, isFromCache = true), dbModel.copy(isFromCache = true))

    private val convertedDataFromServer = listOf(dbModel.copy(isAccountEmpty = true), dbModel)

    private val params = SummaryRepository.Params("", DateRange.SingleDay(mock()), DateRangeString("", ""), null, null)

    @Test
    fun `should only emit data from cache`() {
        val delay = 100L
        whenever(dao.getSummaries(anyInt()))
                .thenReturn(Maybe.just(dataFromCache))
        whenever(service.getSummaries(anyString(), anyString(), anyString(), anyOrNull(), anyOrNull()))
                .thenReturn(Single.error(TestException()))
        whenever(dao.cacheSummary(anyOrNull()))
                .thenReturn(Completable.error(TestException()))
        val repoObservable = repository.getData(params) {
            Observable.error(TestException())
        }
        with(repoObservable.testWithLogging()) {
            scheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS)
            assertInOrder {
                assert { it.isFromCache }
                assert { it.isFromCache }
            }
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    fun `should return data from server`() {
        val delay = 100L
        whenever(dao.getSummaries(anyInt()))
                .thenReturn(Maybe.just(dataFromCache))
        whenever(service.getSummaries(anyString(), anyString(), anyString(), anyOrNull(), anyOrNull()))
                .thenReturn(Single.just(dataFromServer).delay(delay, TimeUnit.MILLISECONDS, scheduler))
        whenever(dao.cacheSummary(anyOrNull()))
                .thenReturn(Completable.complete())
        val repoObservable = repository.getData(params) {
            convertedDataFromServer.toObservable()
        }
        with(repoObservable.testWithLogging()) {
            scheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS)
            assertInOrder {
                assert { !it.isFromCache }
                assert { !it.isFromCache }
            }
            assertNoErrors()
            assertComplete()
        }
    }

    @Test
    fun `should ignore caching error`() {
        val delay = 100L
        whenever(dao.getSummaries(anyInt()))
                .thenReturn(Maybe.just(dataFromCache))
        whenever(service.getSummaries(anyString(), anyString(), anyString(), anyOrNull(), anyOrNull()))
                .thenReturn(Single.just(dataFromServer).delay(delay, TimeUnit.MILLISECONDS, scheduler))
        whenever(dao.cacheSummary(anyOrNull()))
                .thenReturn(Completable.error(TestException()))
        val repoObservable = repository.getData(params) {
            convertedDataFromServer.toObservable()
        }
        with(repoObservable.testWithLogging()) {
            scheduler.advanceTimeBy(delay, TimeUnit.MILLISECONDS)
            assertNoErrors()
            assertComplete()
        }
    }

}
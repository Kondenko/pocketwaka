package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.converters.FetchProjects
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.sql.Date

class GetSummary(
        schedulers: SchedulersContainer,
        private val summaryRepository: SummaryRepository,
        private val getTokenHeader: GetTokenHeaderValue,
        private val dateFormatter: DateFormatter,
        private val summaryResponseConverter: (SummaryRepository.Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel,
        private val timeTrackedConverter: (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel>,
        private val fetchProjects: FetchProjects
) : UseCaseObservable<GetSummary.Params, SummaryDbModel>(schedulers) {

    data class Params(
            val dateRange: DateRange,
            val project: String? = null,
            val branches: String? = null,
            override val refreshRate: Int,
            override val retryAttempts: Int
    ) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = dateRange.end >= dateRange.start
    }

    override fun build(params: Params?): Observable<SummaryDbModel> =
            params?.let(this::getSummary)
                    ?: Observable.error(NullPointerException("Params are null"))

    private fun getSummary(params: Params): Observable<SummaryDbModel> {
        val startDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.start))
        val endDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.end))
        return getTokenHeader.build().flatMapObservable { tokenHeader ->
            val repoParams = SummaryRepository.Params(
                    tokenHeader,
                    startDate,
                    endDate,
                    params.project,
                    params.branches
            )
            summaryRepository.getData(repoParams) { params ->
                flatMap { data -> convert(tokenHeader, params, data) }
            }.scan { t1: SummaryDbModel, t2: SummaryDbModel -> summaryResponseConverter(repoParams, t1, t2) }
        }
    }

    /**
     * Fetches the time tracked for the specified period of time and for each project individually.
     */
    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: Summary): Observable<SummaryDbModel> {
        return data.summaryData
                .toObservable()
                .flatMap {
                    val timeTrackedSource = timeTrackedConverter(params, it).toObservable()
                    val projectsSource = fetchProjects.build(FetchProjects.Params(tokenHeader, it))
                    Observable.concatArrayEagerDelayError(timeTrackedSource, projectsSource)
                }
    }

}
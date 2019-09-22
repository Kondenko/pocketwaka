package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.converters.FetchProjects
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDbModel
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
import java.sql.Date

class GetSummary(
        schedulers: SchedulersContainer,
        private val summaryRepository: SummaryRepository,
        private val getTokenHeader: GetTokenHeaderValue,
        private val dateFormatter: DateFormatter,
        private val summaryResponseConverter: (SummaryRepository.Params, List<Observable<SummaryDbModel>>) -> Observable<SummaryRangeDbModel>,
        private val timeTrackedConverter: (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel>,
        private val fetchProjects: FetchProjects
) : UseCaseObservable<GetSummary.Params, SummaryRangeDbModel>(schedulers) {

    data class Params(
            val dateRange: DateRange,
            val project: String? = null,
            val branches: String? = null,
            override val refreshRate: Int,
            override val retryAttempts: Int
    ) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = dateRange.end >= dateRange.start
    }

    override fun build(params: Params?): Observable<SummaryRangeDbModel> =
            params?.let(this::getSummary)
                    ?: Observable.error(NullPointerException("Params are null"))

    private fun getSummary(params: Params): Observable<SummaryRangeDbModel> {
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
                flatMap { data ->
                    convert(tokenHeader, params, data)
                }
            }
        }
    }

    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: Summary): Observable<SummaryRangeDbModel> {
        return data.summaryData.let { summariesByDays ->
            summariesByDays.map {
                val timeTrackedSource = timeTrackedConverter(params, it).toObservable()
                val projectsSource = fetchProjects.build(FetchProjects.Params(tokenHeader, it))
                timeTrackedSource.concatWith(projectsSource)
            }.let { dbModelsByDays -> summaryResponseConverter(params, dbModelsByDays) }
        }
    }

}
package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.ContinuousCacheBackedRepository
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.summary.model.Project
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRangeString
import com.kondenko.pocketwaka.utils.extensions.dailyRangeTo
import com.kondenko.pocketwaka.utils.extensions.startWithIfNotEmpty
import io.reactivex.Maybe
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class GetSummary(
      private val schedulers: SchedulersContainer,
      private val summaryRepository: ContinuousCacheBackedRepository<SummaryRepository.Params, SummaryData, SummaryDbModel>,
      private val getTokenHeader: UseCaseSingle<Nothing, String>,
      private val dateFormatter: DateFormatter,
      private val summaryResponseConverter: (SummaryRepository.Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel,
      private val timeTrackedConverter: (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel>,
      private val fetchProjects: UseCaseObservable<FetchProjects.Params, Project>
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

    private fun getSummary(params: Params): Observable<SummaryDbModel> =
          getTokenHeader.build()
                .flatMapObservable { tokenHeader ->
                    val startDate = dateFormatter.formatDateAsParameter(params.dateRange.start)
                    val endDate = dateFormatter.formatDateAsParameter(params.dateRange.end)
                    val repoParams = SummaryRepository.Params(
                          tokenHeader,
                          params.dateRange,
                          DateRangeString(startDate, endDate),
                          params.project,
                          params.branches
                    )
                    summaryRepository.getData(repoParams) { params ->
                        flatMapObservable { data -> convert(tokenHeader, params, data) }
                    }
                          .scan { t1: SummaryDbModel, t2: SummaryDbModel -> summaryResponseConverter(repoParams, t1, t2) }
                          .debounce(200, TimeUnit.MILLISECONDS, schedulers.uiScheduler)
                }
                .subscribeOn(schedulers.workerScheduler)


    /**
     * Fetches the time tracked for the specified period of time and for each project individually.
     */
    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: SummaryData): Observable<SummaryDbModel> {
        val timeTrackedSource = timeTrackedConverter(params, data).toObservable()
        val projectObservables = params.dateRange.run { start dailyRangeTo end }.map {
            fetchProjects.build(FetchProjects.Params(tokenHeader, DateRange.SingleDay(it), data))
        }.toTypedArray().let {
            Observable.mergeArray(*it)
                  .map(SummaryUiModel::ProjectItem)
                  .cast(SummaryUiModel::class.java)
                  .startWithIfNotEmpty(SummaryUiModel.ProjectsTitle)
                  .map {
                      SummaryDbModel(params.dateRange.hashCode().toLong(), data = listOf(it))
                  }
        }
        return projectObservables.startWith(timeTrackedSource)
    }


}
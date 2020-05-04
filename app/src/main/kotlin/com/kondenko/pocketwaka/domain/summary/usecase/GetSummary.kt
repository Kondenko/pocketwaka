package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.ContinuousCacheBackedRepository
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.summary.model.Branch
import com.kondenko.pocketwaka.domain.summary.model.Commit
import com.kondenko.pocketwaka.domain.summary.model.Project
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRangeString
import com.kondenko.pocketwaka.utils.extensions.dailyRangeTo
import io.reactivex.Maybe
import io.reactivex.Observable

class GetSummary(
      private val schedulers: SchedulersContainer,
      private val summaryRepository: ContinuousCacheBackedRepository<SummaryRepository.Params, SummaryData, SummaryDbModel>,
      private val getTokenHeader: UseCaseSingle<Nothing, String>,
      private val dateFormatter: DateFormatter,
      private val summaryResponseConverter: (SummaryRepository.Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel,
      private val timeTrackedConverter: (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel>,
      private val fetchProjects: UseCaseObservable<FetchProjects.Params, Project>,
      private val projectsToUiModels: UseCaseObservable<ProjectsToUiModels.Params, SummaryDbModel>
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
                        flatMapObservable { data ->
                            convert(tokenHeader, params, data)
                        }
                    }.scan { t1: SummaryDbModel, t2: SummaryDbModel -> summaryResponseConverter(repoParams, t1, t2) }
                }
                .subscribeOn(schedulers.workerScheduler)


    /**
     * Fetches the time tracked for the specified period of time and for each project individually.
     */
    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: SummaryData): Observable<SummaryDbModel> {
        val timeTrackedSource = timeTrackedConverter(params, data).toObservable()
        val projectsSource: Observable<SummaryDbModel> = params.dateRange
              .run { start dailyRangeTo end }
              .map {
                  fetchProjects.build(FetchProjects.Params(tokenHeader, DateRange.SingleDay(it), data))
              }
              .reduce { a, b -> a.mergeWith(b) }
              .groupBy { it.name }
              .flatMap {
                  it.reduce { t1: Project, t2: Project -> t1.merge(t2) }.toObservable()
              }
              .let { projectsToUiModels.build(ProjectsToUiModels.Params(params.dateRange, it)) }
        return Observable.concatArrayEagerDelayError(timeTrackedSource, projectsSource)
    }

    private fun Project.merge(other: Project): Project {
        require(name == other.name) {
            "Projects are different"
        }
        require(totalSeconds == other.totalSeconds) {
            /* See [SummaryRepositoryKt.plus(StatsEntity, StatsEntity)]  */
            "Project totalSeconds values should have already been merged when fetching summaries"
        }
        return Project(
              name,
              totalSeconds,
              isRepoConnected && other.isRepoConnected,
              branches.merge(other.branches)
        )
    }

    private fun List<Branch>.merge(other: List<Branch>): List<Branch> =
          (this + other)
                .groupBy { it.name }
                .map { (_, branches) -> branches.reduce { a, b -> a.merge(b) } }

    private fun Branch.merge(other: Branch): Branch {
        require(name == other.name) { "Branches are different" }
        return Branch(
              name,
              totalSeconds + other.totalSeconds,
              commits.mergeCommits(other.commits)
        )
    }

    private fun List<Commit>.mergeCommits(other: List<Commit>): List<Commit> =
          (this + other)
                .groupBy { it.message }
                .map { (_, branches) -> branches.reduce { a, b -> a.merge(b) } }

    private fun Commit.merge(other: Commit): Commit {
        require(hash == other.hash) { "Messages are different" }
        require(message == other.message) { "Messages are different" }
        return Commit(hash, message, totalSeconds + other.totalSeconds)
    }

}
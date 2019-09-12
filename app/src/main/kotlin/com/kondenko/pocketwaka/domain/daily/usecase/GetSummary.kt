package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.branches.DurationsRepository
import com.kondenko.pocketwaka.data.branches.model.Duration
import com.kondenko.pocketwaka.data.commits.CommitsRepository
import com.kondenko.pocketwaka.data.commits.model.CommitsServiceModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDbModel
import com.kondenko.pocketwaka.data.summary.model.server.Summary
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.domain.daily.model.Branch
import com.kondenko.pocketwaka.domain.daily.model.Commit
import com.kondenko.pocketwaka.domain.daily.model.Project
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Maybes
import io.reactivex.rxkotlin.toObservable
import java.sql.Date
import kotlin.math.roundToLong
import com.kondenko.pocketwaka.data.commits.model.Commit as CommitServerModel

class GetSummary(
        schedulers: SchedulersContainer,
        private val summaryRepository: SummaryRepository,
        private val durationsRepository: DurationsRepository,
        private val commitsRepository: CommitsRepository,
        private val getTokenHeader: GetTokenHeaderValue,
        private val dateFormatter: DateFormatter,
        private val summaryResponseConverter: (SummaryRepository.Params, List<Maybe<SummaryDbModel>>) -> Maybe<SummaryRangeDbModel>,
        private val timeTrackedConverter: (SummaryRepository.Params, SummaryData) -> Maybe<SummaryDbModel>
) : UseCaseObservable<GetSummary.Params, SummaryRangeDbModel>(schedulers) {

    private val noRepoError = "This project is not associated with a repository"

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
        // STOPSHIP
        val startDate =  dateFormatter.formatDateAsParameter(Date(params.dateRange.start)) // "2019-09-05"
        val endDate =  dateFormatter.formatDateAsParameter(Date(params.dateRange.end)) // "2019-09-05"
        return getTokenHeader.build().flatMapObservable { tokenHeader ->
            val repoParams = SummaryRepository.Params(
                    tokenHeader,
                    startDate,
                    endDate,
                    params.project,
                    params.branches
            )
            summaryRepository.getData(repoParams) { params, data ->
                convert(tokenHeader, params, data)
            }
        }
    }

    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: Summary): Maybe<SummaryRangeDbModel> {
        return data.summaryData.let { summariesByDays ->
            summariesByDays.map {
                val timeTrackedMaybe = timeTrackedConverter(params, it)
                val projectsMaybe = getProjects(tokenHeader, it)
                Maybes.zip(timeTrackedMaybe, projectsMaybe, this::merge)
            }.let { dbModelsByDays -> summaryResponseConverter(params, dbModelsByDays) }
        }
    }

    private fun merge(timeTrackedModel: SummaryDbModel, projectsModel: SummaryDbModel): SummaryDbModel =
            SummaryDbModel(
                    date = timeTrackedModel.date,
                    isFromCache = timeTrackedModel.isFromCache && projectsModel.isFromCache,
                    isEmpty = timeTrackedModel.isEmpty && projectsModel.isEmpty,
                    data = timeTrackedModel.data + projectsModel.data
            )

    private fun getProjects(token: String, summaryData: SummaryData): Maybe<SummaryDbModel> =
            summaryData.projects
                    .toObservable()
                    .filter { it.name != null }
                    .flatMapMaybe { project ->
                        val date = summaryData.range.date
                        val projectName = project.name
                                ?: throw NullPointerException("A project with a null name wasn't filtered out: $project")
                        val branchesSingle =
                                durationsRepository.getData(DurationsRepository.Params(token, date, projectName))
                                        .toMaybe()
                        val commitsSingle =
                                commitsRepository.getData(CommitsRepository.Params(token, projectName))
                                        .toMaybe()
                                        .onErrorComplete()
                        Maybes.zip(branchesSingle, commitsSingle) { branchesServerModel, commitsServerModel ->
                            val timeTracked = project.totalSeconds
                                    ?.roundToLong()
                                    ?.let(dateFormatter::secondsToHumanReadableTime)
                            val isRepoConnected = commitsServerModel.isRepoConnected()
                            val commits = commitsServerModel.commits.filter { it.authorDate.contains(date) }
                            val branches = groupByBranches(
                                    branchesServerModel.branchesData,
                                    commits
                            )
                            Project(projectName, timeTracked, isRepoConnected, branches)
                        }
                    }
                    .toList()
                    .flatMapMaybe { projects ->
                        if (projects.isNotEmpty()) {
                            Maybe.just(listOf(SummaryUiModel.ProjectsTitle) + SummaryUiModel.Projects(projects))
                        } else {
                            Maybe.empty()
                        }
                    }
                    .map { SummaryDbModel(summaryData.range.date, false, it.isEmpty(), it) }

    private fun groupByBranches(branches: Iterable<Duration>, commits: Iterable<CommitServerModel>): List<Branch> =
            branches
                    .groupBy { it.branch }
                    .map { (name, durations) -> name to durations.sumByDouble { it.duration } }
                    .filter { (branch, duration) -> branch != null }
                    .map { (branch: String?, duration) ->
                        val durationHumanReadable = dateFormatter.secondsToHumanReadableTime(duration.roundToLong())
                        val commitsFromBranch = branch?.let { name ->
                            commits
                                    .filter { it.ref?.contains(name) == true }
                                    .map { Commit(it.message, dateFormatter.secondsToHumanReadableTime(it.totalSeconds.toLong())) }
                        }
                        Branch(branch, durationHumanReadable, commitsFromBranch)
                    }

    private fun CommitsServiceModel.isRepoConnected() = error?.contains(noRepoError, ignoreCase = true) == false

}
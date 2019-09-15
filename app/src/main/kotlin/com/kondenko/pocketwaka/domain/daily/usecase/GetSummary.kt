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
import com.kondenko.pocketwaka.domain.daily.model.*
import com.kondenko.pocketwaka.utils.KOptional
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
        val startDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.start)) // "2019-09-05"
        val endDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.end)) // "2019-09-05"
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
                                        .map { KOptional.of(it) }
                                        .onErrorReturnItem(KOptional.empty())
                                        .defaultIfEmpty(KOptional.empty())
                        val commitsSingle =
                                commitsRepository.getData(CommitsRepository.Params(token, projectName))
                                        .toMaybe()
                                        .onErrorComplete()
                                        .map { KOptional.of(it) }
                                        .onErrorReturnItem(KOptional.empty())
                                        .defaultIfEmpty(KOptional.empty())
                        Maybes.zip(branchesSingle, commitsSingle) { branchesServerModel, commitsServerModel ->
                            val timeTracked = project.totalSeconds
                                    ?.roundToLong()
                                    ?.let { seconds -> dateFormatter.secondsToHumanReadableTime(seconds, DateFormatter.Format.Short) }
                            val isRepoConnected = commitsServerModel.item?.isRepoConnected() == true
                            val commits = (commitsServerModel.item?.commits ?: emptyList())
                                    .filter { it.authorDate.contains(date) }
                            val branchesData = branchesServerModel?.item?.branchesData
                                    ?: emptyList()
                            val branches = groupByBranches(
                                    branchesData,
                                    commits
                            )
                            Project(projectName, timeTracked, isRepoConnected, branches)
                        }
                    }
                    .toList()
                    .flatMapMaybe { projects ->
                        if (projects.isNotEmpty()) {
                            Maybe.just<List<SummaryUiModel>>(
                                    listOf(SummaryUiModel.ProjectsTitle) +
                                            projects.map<Project, SummaryUiModel.Project> {
                                                SummaryUiModel.Project(it.toUiModel())
                                            }
                            )
                        } else {
                            Maybe.empty()
                        }
                    }
                    .map { SummaryDbModel(summaryData.range.date, false, it.isEmpty(), it) } // TODO specify if the data is from cache and if it's empty

    private fun Project.toUiModel(): List<ProjectModel> {
        var projectModel = listOf(ProjectModel.ProjectName(name, timeTracked)) +
                branches.flatMap {
                    listOf(ProjectModel.Branch(it.name, it.timeTracked)) +
                            if (it.commits.isEmpty() && isRepoConnected) {
                                listOf(ProjectModel.NoCommitsLabel)
                            } else {
                                it.commits.map<Commit, ProjectModel> { (message, timeTracked) ->
                                    ProjectModel.Commit(message, timeTracked)
                                }
                            }
                }
        if (!isRepoConnected && branches.isNotEmpty()) {
            projectModel = projectModel + listOf(ProjectModel.ConnectRepoAction(connectRepoLink(name)))
        }
        return projectModel
    }

    private fun groupByBranches(branches: Iterable<Duration>, commits: Iterable<CommitServerModel>): List<Branch> =
            branches
                    .groupBy { it.branch }
                    .map { (name, durations) -> name to durations.sumByDouble { it.duration } }
                    .filter { (branch, _) -> branch != null }
                    .map { (branch: String, duration) ->
                        val durationHumanReadable = dateFormatter.secondsToHumanReadableTime(
                                duration.roundToLong(),
                                DateFormatter.Format.Short
                        )
                        val commitsFromBranch = commits
                                .filter { it.ref?.contains(branch) == true }
                                .map {
                                    Commit(it.message, dateFormatter.secondsToHumanReadableTime(
                                            it.totalSeconds.toLong(),
                                            DateFormatter.Format.Short
                                    ))
                                }
                        Branch(branch, durationHumanReadable, commitsFromBranch)
                    }

    private fun CommitsServiceModel.isRepoConnected() = error == null || !error.contains(noRepoError, ignoreCase = true)

    private fun connectRepoLink(project: String) = "https://wakatime.com/projects/$project/edit"

}
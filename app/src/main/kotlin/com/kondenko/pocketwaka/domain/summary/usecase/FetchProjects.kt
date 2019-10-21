package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.branches.DurationsRepository
import com.kondenko.pocketwaka.data.branches.model.Duration
import com.kondenko.pocketwaka.data.commits.CommitsRepository
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.summary.model.*
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import com.kondenko.pocketwaka.utils.extensions.concatMapEagerDelayError
import com.kondenko.pocketwaka.utils.extensions.startWithIfNotEmpty
import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.toObservable
import kotlin.math.roundToLong

class FetchProjects(
      private val schedulersContainer: SchedulersContainer,
      private val durationsRepository: DurationsRepository,
      private val commitsRepository: CommitsRepository,
      private val dateFormatter: DateFormatter
) : UseCaseObservable<FetchProjects.Params, SummaryDbModel>(schedulersContainer) {

    private val noRepoError = "This project is not associated with a repository"

    data class Params(val tokenHeader: String, val summaryData: SummaryData)

    override fun build(params: Params?): Observable<SummaryDbModel> {
        val date = params?.summaryData?.range?.date?.let(dateFormatter::parseDateParameter)
        return if (params != null && date != null) {
            getProjects(params.tokenHeader, params.summaryData).map {
                SummaryDbModel(date, data = listOf(it))
            }
        } else {
            Observable.error(NullPointerException("Params are null"))
        }
    }

    private fun getProjects(token: String, summaryData: SummaryData): Observable<SummaryUiModel> =
          summaryData.projects
                .toObservable()
                .filter { it.name != null }
                .concatMapEagerDelayError { project: StatsEntity ->
                    val date = summaryData.range.date
                    val projectName = project.name
                          ?: throw NullPointerException("A project with a null name wasn't filtered out: $project")
                    val branchesSingle =
                          durationsRepository.getData(DurationsRepository.Params(token, date, projectName))
                                .subscribeOn(schedulersContainer.workerScheduler)
                                .map { KOptional.of(it) }
                                .onErrorReturnItem(KOptional.empty())
                    val commitsSingle =
                          commitsRepository.getData(CommitsRepository.Params(token, projectName))
                                .subscribeOn(schedulersContainer.workerScheduler)
                                .map { true to it }
                                .onErrorReturn {
                                    val isRepoConnected = (it as? WakatimeException)?.message?.contains(noRepoError, true) == false
                                    isRepoConnected to emptyList()
                                }
                    Singles.zip(branchesSingle, commitsSingle) { branchesServerModel, (isRepoConnected, commits) ->
                        val timeTracked = project.totalSeconds
                              ?.roundToLong()
                              ?.let { seconds -> dateFormatter.secondsToHumanReadableTime(seconds, DateFormatter.Format.Short) }
                        val commitsForDate = commits.filter { it.authorDate.contains(date) }
                        val branchesData = branchesServerModel?.item?.branchesData
                              ?: emptyList()
                        val branches = groupByBranches(
                              branchesData,
                              commitsForDate
                        )
                        Project(projectName, timeTracked, isRepoConnected, branches)
                    }.toObservable()
                }
                .map { it.toUiModel() }
                .startWithIfNotEmpty(SummaryUiModel.ProjectsTitle)

    private fun Project.toUiModel(): SummaryUiModel {
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
        return SummaryUiModel.Project(projectModel)
    }

    private fun groupByBranches(branches: Iterable<Duration>, commits: Iterable<com.kondenko.pocketwaka.data.commits.model.Commit>): List<Branch> =
          branches
                .groupBy { it.branch }
                .map { (name, durations) -> name to durations.sumByDouble { it.duration } }
                .filter { (branch, _) -> branch != null }
                .map { (branch: String?, duration) ->
                    val durationHumanReadable = dateFormatter.secondsToHumanReadableTime(
                          duration.roundToLong(),
                          DateFormatter.Format.Short
                    )
                    val commitsFromBranch = commits
                          .filter { it.ref?.contains(branch!!) == true }
                          .map {
                              Commit(it.message, dateFormatter.secondsToHumanReadableTime(
                                    it.totalSeconds.toLong(),
                                    DateFormatter.Format.Short
                              ))
                          }
                    Branch(branch!!, durationHumanReadable, commitsFromBranch)
                }

    private fun connectRepoLink(project: String) = "https://wakatime.com/projects/$project/edit"

}
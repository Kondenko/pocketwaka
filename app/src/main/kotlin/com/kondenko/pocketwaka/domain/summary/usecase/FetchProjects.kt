package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.branches.DurationsRepository
import com.kondenko.pocketwaka.data.branches.model.Duration
import com.kondenko.pocketwaka.data.branches.model.DurationsServerModel
import com.kondenko.pocketwaka.data.commits.CommitsRepository
import com.kondenko.pocketwaka.data.commits.model.CommitServerModel
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.summary.model.Branch
import com.kondenko.pocketwaka.domain.summary.model.Commit
import com.kondenko.pocketwaka.domain.summary.model.Project
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.exceptions.WakatimeException
import com.kondenko.pocketwaka.utils.extensions.concatMapEagerDelayError
import com.kondenko.pocketwaka.utils.types.KOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.toObservable

class FetchProjects(
      private val schedulersContainer: SchedulersContainer,
      private val durationsRepository: DurationsRepository,
      private val commitsRepository: CommitsRepository,
      private val dateFormatter: DateFormatter
) : UseCaseObservable<FetchProjects.Params, Project>(schedulersContainer) {

    private val noRepoError = "This project is not associated with a repository"

    data class Params(val tokenHeader: String, val date: DateRange.SingleDay, val summaryData: SummaryData)

    override fun build(params: Params?): Observable<Project> = params?.run {
        getProjects(tokenHeader, dateFormatter.formatDateAsParameter(date.date), summaryData)
    } ?: Observable.error(NullPointerException("Params are null"))

    private fun getProjects(token: String, date: String, summaryData: SummaryData): Observable<Project> =
          summaryData.projects
                .toObservable()
                .filter { it.name != null }
                .concatMapEagerDelayError { project: StatsEntity -> getBranchesAndCommits(date, token, project) }

    private fun getBranchesAndCommits(date: String, token: String, project: StatsEntity): Observable<Project> {
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
        return Singles.zip(branchesSingle, commitsSingle) { branchesServerModel: KOptional<DurationsServerModel>, (isRepoConnected, commits): Pair<Boolean, List<CommitServerModel>> ->
            zipBranchesAndCommits(project, date, branchesServerModel, isRepoConnected, commits)
        }.toObservable()
    }

    private fun zipBranchesAndCommits(
          project: StatsEntity,
          date: String,
          branchesServerModel: KOptional<DurationsServerModel>,
          isRepoConnected: Boolean,
          commits: List<CommitServerModel>
    ): Project {
        val commitsForDate =
              commits
                    .filter { it.authorDate.contains(date) }
                    .filter { it.totalSeconds > 0 }
        val branchesData = branchesServerModel.item?.branchesData
              ?: emptyList()
        val branches = groupByBranches(
              branchesData,
              commitsForDate
        )
        return Project(project.name!!, project.totalSeconds, isRepoConnected, branches)
    }

    private fun groupByBranches(branches: Iterable<Duration>, commits: Iterable<CommitServerModel>): List<Branch> =
          branches
                .groupBy { it.branch }
                .map { (name, durations) -> name to durations.sumByDouble { it.duration } }
                .filter { (branch, _) -> branch != null }
                .map { (branch: String?, duration) ->
                    val commitsFromBranch = commits
                          .filter { it.ref?.contains(branch!!) == true }
                          .map {
                              Commit(it.message, it.totalSeconds)
                          }
                    Branch(branch!!, duration.toFloat(), commitsFromBranch)
                }

}
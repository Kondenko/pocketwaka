package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.branches.DurationsRepository
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
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.concatMapEagerDelayError
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.HttpException
import java.net.HttpURLConnection

class FetchProjects(
      private val schedulersContainer: SchedulersContainer,
      private val durationsRepository: DurationsRepository,
      private val commitsRepository: CommitsRepository,
      private val dateFormatter: DateFormatter
) : UseCaseObservable<FetchProjects.Params, Project>(schedulersContainer) {

    private val noRepoError = "This project is not associated with a repository"

    data class Params(val tokenHeader: String, val date: DateRange.SingleDay, val summaryData: SummaryData)

    override fun build(params: Params?): Observable<Project> = params?.run {
        getProjects(tokenHeader, date.date, summaryData)
    } ?: Observable.error(NullPointerException("Params are null"))

    private fun getProjects(token: String, date: LocalDate, summaryData: SummaryData): Observable<Project> =
          summaryData.projects
                .toObservable()
                .filter { it.name != null }
                .concatMapEagerDelayError { project: StatsEntity -> gatherProjectData(date, token, project) }

    private fun gatherProjectData(date: LocalDate, token: String, projectEntity: StatsEntity): Observable<Project> {
        val projectName = projectEntity.name
              ?: throw NullPointerException("A project with a null name wasn't filtered out: $projectEntity")
        val projectObservable: Observable<Project> = Observable.just(
              Project(
                    projectName,
                    projectEntity.totalSeconds.toLong(),
                    isRepoConnected = false,
                    branches = emptyMap(),
                    repositoryUrl = connectRepoLink(projectName)
              )
        )
        // TODO Total time branches doesn't match project's time
        val projectWithBranchesObservable: Observable<Project> = projectObservable.flatMap { project ->
            getBranches(date, token, projectName)
                  .toObservable()
                  .map { branches ->
                      project.copy(branches = branches.associateBy { it.name })
                  }
        }
        val projectWithCommitsObservable: Observable<Project> = projectWithBranchesObservable.flatMap { project ->
            Observable.fromIterable(project.branches.values)
                  .concatMapEagerDelayError { branch ->
                      getCommits(date, token, projectName, branch.name)
                            .onErrorReturn {
                                // TODO Add actual implementation of this
                                val isRepoConnected = (it as? HttpException)?.code() != HttpURLConnection.HTTP_FORBIDDEN
                                WakaLog.d("Repo connected for $projectName == $isRepoConnected")
                                emptyList()
                            }
                            .map { newCommits ->
                                val updatedCommits = branch.commits?.let { it + newCommits } ?: newCommits
                                branch.copy(commits = updatedCommits).also {
                                    WakaLog.d("""
                                        Commits in ${branch.name} have changed:
                                        BEFORE ${branch.commits?.map { it.message }?.joinToString()}
                                        AFTER ${it.commits?.map { it.message }?.joinToString()}
                                    """.trimIndent())
                                }
                            }
                  }
                  .map {
                      val updatedBranches = project.branches + mapOf(it.name to it)
                      project.copy(branches = updatedBranches).also {
                          WakaLog.d("""
                                        Branches in ${project.name} have changed:
                                        BEFORE ${project.branches}
                                        AFTER ${it.branches}
                                    """.trimIndent())
                      }
                  }
        }
        return Observable.concatArray(projectObservable, projectWithBranchesObservable, projectWithCommitsObservable)
    }

    private fun getBranches(date: LocalDate, token: String, projectName: String): Single<List<Branch>> =
          durationsRepository.getData(DurationsRepository.Params(token, dateFormatter.formatDateAsParameter(date), projectName))
                .subscribeOn(schedulersContainer.workerScheduler)
                .flatMapObservable { durationsModel -> groupByBranches(durationsModel).toObservable() }
                .toList()
                .onErrorReturnItem(emptyList())

    private fun getCommits(date: LocalDate, token: String, projectName: String, branch: String): Observable<List<Commit>> =
          commitsRepository.getData(CommitsRepository.Params(token, projectName, branch))
                .subscribeOn(schedulersContainer.workerScheduler)
                .flatMap { it.toObservable() }
                .filter { it.getLocalDate() == date }
// TODO Don't load ALL the commits
//                .takeUntil {
//                    (it.getLocalDate().isBefore(date)).apply {
//                        WakaLog.d("Stop loading commits at $it")
//                    }
//                }
                .map { it.toUiModel() }
                .toList()
                .toObservable()

    private fun groupByBranches(durations: DurationsServerModel): List<Branch> =
          durations.branchesData
                .groupBy { it.branch }
                .map { (name, durations) -> name to durations.sumByDouble { it.duration } }
                .filter { (branch, _) -> branch != null }
                .map { (branch: String?, duration) -> Branch(branch!!, duration.toLong(), null) }

    private fun CommitServerModel.toUiModel() = Commit(hash, message, totalSeconds.toLong())

    private fun connectRepoLink(project: String) = "https://wakatime.com/projects/$project/edit"

    private fun CommitServerModel.getLocalDate() = LocalDate.parse(authorDate, DateTimeFormatter.ISO_DATE_TIME)

}
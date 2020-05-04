package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.summary.model.Commit
import com.kondenko.pocketwaka.domain.summary.model.Project
import com.kondenko.pocketwaka.domain.summary.model.ProjectModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.startWithIfNotEmpty
import io.reactivex.Observable
import kotlin.math.roundToLong

class ProjectsToUiModels(
      schedulersContainer: SchedulersContainer,
      private val dateFormatter: DateFormatter
) : UseCaseObservable<ProjectsToUiModels.Params, SummaryDbModel>(schedulersContainer) {

    data class Params(val date: DateRange, val projects: Observable<Project>)

    override fun build(params: Params?): Observable<SummaryDbModel> {
        return (params?.projects ?: Observable.error(java.lang.NullPointerException("Null params")))
              .map { it.toUiModel() }
              .startWithIfNotEmpty(SummaryUiModel.ProjectsTitle)
              .map {
                  SummaryDbModel(
                        params?.date?.hashCode()?.toLong()
                              ?: throw NullPointerException("Null date passed"),
                        data = listOf(it))
              }
    }

    private fun Project.toUiModel(): SummaryUiModel {
        var projectModel = listOf(ProjectModel.ProjectName(name, totalSeconds.toHumanReadable())) +
              branches.flatMap {
                  listOf(ProjectModel.Branch(it.name, it.totalSeconds.toHumanReadable())) +
                        if (it.commits.isEmpty() && isRepoConnected) {
                            listOf(ProjectModel.NoCommitsLabel)
                        } else {
                            it.commits.map<Commit, ProjectModel> { (message, timeTracked) ->
                                ProjectModel.Commit(message, timeTracked.toFloat().toHumanReadable())
                            }
                        }
              }
        if (!isRepoConnected && branches.isNotEmpty()) {
            projectModel = projectModel + listOf(ProjectModel.ConnectRepoAction(connectRepoLink(name)))
        }
        return SummaryUiModel.Project(projectModel)
    }

    private fun Float.toHumanReadable() = this
          .roundToLong()
          .let { seconds -> dateFormatter.secondsToHumanReadableTime(seconds, DateFormatter.Format.Short) }

    private fun connectRepoLink(project: String) = "https://wakatime.com/projects/$project/edit"

}
package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.commits.CommitsRepository
import com.kondenko.pocketwaka.data.commits.model.Commits
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
import com.kondenko.pocketwaka.utils.extensions.substringOrNull
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.rxkotlin.Maybes
import io.reactivex.rxkotlin.toObservable
import java.sql.Date

class GetSummary(
        schedulers: SchedulersContainer,
        private val summaryRepository: SummaryRepository,
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
            summaryRepository.getData(repoParams) { params, data ->
                convert(tokenHeader, params, data)
            }
        }
    }

    private fun convert(tokenHeader: String, params: SummaryRepository.Params, data: Summary): Maybe<SummaryRangeDbModel> {
        return data.summaryData.let {
            it.map {
                val timeTrackedMaybe = timeTrackedConverter(params, it)
                val projectsMaybe = getProjects(tokenHeader, it)
                Maybes.zip(timeTrackedMaybe, projectsMaybe, this::merge)
            }.let { summaryResponseConverter(params, it) }
        }
    }

    private fun merge(timeTrackedModel: SummaryDbModel, projectsModel: SummaryDbModel): SummaryDbModel =
            SummaryDbModel(
                    date = timeTrackedModel.date,
                    isFromCache = timeTrackedModel.isFromCache && projectsModel.isFromCache,
                    isEmpty = timeTrackedModel.isEmpty && projectsModel.isEmpty,
                    data = timeTrackedModel.data + projectsModel.data
            )

    private fun getProjects(tokenHeader: String, summaryData: SummaryData): Maybe<SummaryDbModel> =
            summaryData.projects
                    .toObservable()
                    .filter { it.name != null }
                    .flatMapSingle { project ->
                        val name = project.name
                                ?: throw NullPointerException("A project with a null name wasn't filtered out: $project")
                        commitsRepository.getData(CommitsRepository.Params(tokenHeader, name))
                                .map { commits ->
                                    val timeTracked = project.totalSeconds?.let { dateFormatter.formatDateForDisplay(it.toInt()) }
                                    val isRepoConnected = commits.isRepoConnected()
                                    val branches = commits.groupByBranches()
                                    Project(name, timeTracked, isRepoConnected, branches)
                                }
                    }
                    .toList()
                    .flatMapMaybe {
                        if (it.isNotEmpty()) {
                            Maybe.just(listOf(SummaryUiModel.ProjectsSubtitle) + SummaryUiModel.Projects(it))
                        } else {
                            Maybe.empty()
                        }
                    }
                    .map { SummaryDbModel(summaryData.range.date, false, it.isEmpty(), it) }

    private fun Commits.isRepoConnected() = error?.contains(noRepoError, ignoreCase = true) == true

    private fun Commits.groupByBranches(): List<Branch> {
        return this.commits.groupBy { it.ref }.mapNotNull { (ref, commitsServerModel) ->
            ref?.let {
                val name = it.refToBranchName()
                val timeTracked = commitsServerModel.sumBy { it.totalSeconds }.let(dateFormatter::formatDateForDisplay)
                val commits = commitsServerModel.map { Commit(it.message, dateFormatter.formatDateForDisplay(it.totalSeconds)) }
                Branch(name, timeTracked, commits)
            }
        }
    }

    private fun String?.refToBranchName(): String {
        val branchNameDelimiter = "head"
        return this
                ?.substringOrNull(0, indexOf(branchNameDelimiter) + branchNameDelimiter.length)
                ?: this
                ?: commitsRepository.getUnknownBranchName()
    }

}
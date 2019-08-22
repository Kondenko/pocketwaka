package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.StatsRange
import com.kondenko.pocketwaka.data.ModelConverter
import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.common.model.database.StatsEntity
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.server.SummaryData
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.daily.model.Branch
import com.kondenko.pocketwaka.domain.daily.model.Commit
import com.kondenko.pocketwaka.domain.daily.model.Project
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.daily.usecase.GetAverage
import kotlin.math.roundToInt

/**
 * Converts a summary of a single day to a DTO.
 */
class SummaryDataConverter(
        private val getAverage: GetAverage,
        private val dateFormatter: DateFormatter
) : ModelConverter<SummaryRepository.Params, SummaryData, SummaryDbModel> {

    private val averageRange = StatsRange.Month

    override fun convert(model: SummaryData, param: SummaryRepository.Params): SummaryDbModel {
        val isEmpty = model.grandTotal.totalSeconds == 0f
        val uiModels = mutableListOf<SummaryUiModel>()
        uiModels += convertTimeTracked(model)
        val projects = convertProjects(model)
        if (projects.isNotEmpty()) {
            uiModels += SummaryUiModel.ProjectsSubtitle
            uiModels += SummaryUiModel.Projects(projects)
        }
        return SummaryDbModel(model.range.date, false, isEmpty, uiModels)
    }

    private fun convertProjects(model: SummaryData): List<Project> {
        return model.projects.mapNotNull {
            // TODO Format as Xh Ym instead of Xhrs Ymin
            val time = it.totalSeconds?.roundToInt()?.let(dateFormatter::formatDateForDisplay)
            val branches = getBranches(model, it)
            val isRepoConnected = branches.isNotEmpty()
            it.name?.let { name ->
                Project(name, time, isRepoConnected, branches)
            }
        }
    }

    private fun getBranches(model: SummaryData, project: StatsEntity): List<Branch> {
        return emptyList()
    }

    private fun getCommits(model: SummaryData, branch: Branch): List<Commit> {
        return emptyList()
    }

    private fun convertTimeTracked(model: SummaryData): SummaryUiModel.TimeTracked {
        val totalSeconds = model.grandTotal.totalSeconds.roundToInt()
        val averageSeconds = getAverage(averageRange).blockingGet() // This method is executed on a background thread
        val averageDelta = getAverageDelta(totalSeconds, averageSeconds)
        val formattedTime = dateFormatter.formatDateForDisplay(totalSeconds)
        return SummaryUiModel.TimeTracked(formattedTime, averageDelta)
    }

    private fun getAverageDelta(totalSeconds: Int, averageSec: Int): Int {
        return totalSeconds * 100 / averageSec - 100
    }

}
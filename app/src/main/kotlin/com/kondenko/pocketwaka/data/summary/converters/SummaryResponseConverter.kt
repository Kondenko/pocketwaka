package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel.ProjectItem
import com.kondenko.pocketwaka.utils.WakaLog

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DbModel.
 */
class SummaryResponseConverter : (SummaryRepository.Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel {

    override fun invoke(param: SummaryRepository.Params, first: SummaryDbModel, second: SummaryDbModel): SummaryDbModel =
          SummaryDbModel(
                date = first.date,
                isEmpty = first.isEmpty == true || second.isEmpty == true,
                isAccountEmpty = first.isAccountEmpty == true || second.isAccountEmpty == true,
                isFromCache = first.isFromCache || second.isFromCache,
                data = first.data merge second.data
          )


    // TODO Make sure projects order is maintained
    private infix fun List<SummaryUiModel>.merge(other: List<SummaryUiModel>): List<SummaryUiModel> {
        val newList = mutableListOf<SummaryUiModel>()
        forEach { item ->
            if (item !is ProjectItem || !item.isInOtherList(other)) {
                newList.add(item)
            }
        }
        newList.addAll(other)
        return newList.also {
            WakaLog.d("Merging projects:\nOLD: ${this.projects()}\nNEW: ${other.projects()}\nRESULT: ${it.projects()}")
        }
    }

    fun ProjectItem.isInOtherList(other: List<SummaryUiModel>) =
          other.filterIsInstance<ProjectItem>().find { this.model.name == it.model.name } != null

    private fun List<SummaryUiModel>.projects() = filterIsInstance<ProjectItem>()
          .map { it.model }
          .map {
              "${it.name} (${it.branches.size} branches, ${it.branches.values.sumBy { it.commits?.size ?: 0 }} commits)"
          }

}


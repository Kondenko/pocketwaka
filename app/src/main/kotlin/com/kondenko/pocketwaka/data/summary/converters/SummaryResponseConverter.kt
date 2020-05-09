package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel.ProjectItem
import com.kondenko.pocketwaka.domain.summary.model.mergeBranches
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
/*
    private infix fun List<SummaryUiModel>.merge(other: List<SummaryUiModel>): List<SummaryUiModel> =
          (other.reversed() + this.reversed())
                .distinctBy { if (it is ProjectItem) it.model.name else it }
                .reversed()
                .also {
                    WakaLog.d("Merging projects:\nOLD: ${this.projects()}\nNEW: ${other.projects()}\nRESULT: ${it.projects()}")
                }
*/

    // TODO This is where projects with different lists of commits replace one another
    private infix fun List<SummaryUiModel>.merge(other: List<SummaryUiModel>): List<SummaryUiModel> {
        val newList = mutableListOf<SummaryUiModel>()
        forEach { item ->
            val projectInOtherList = (item as? ProjectItem)?.projectInOtherList(other)
            if (item !is ProjectItem || projectInOtherList == null) {
                newList.add(item)
            }
        }
        other.forEach {
            val projectInOtherList = (it as? ProjectItem)?.projectInOtherList(this)
            if (it is ProjectItem && projectInOtherList != null) {
                newList.add(ProjectItem(it.model))
            } else {
                newList.add(it)
            }
        }
        return newList.also {
             WakaLog.d("Merging projects:\nOLD: ${this.projects()}\nNEW: ${other.projects()}\nRESULT: ${it.projects()}")
        }
    }


    fun ProjectItem.projectInOtherList(other: List<SummaryUiModel>) =
          other.filterIsInstance<ProjectItem>().find { this.model.name == it.model.name }

    private fun List<SummaryUiModel>.projects() = filterIsInstance<ProjectItem>()
          .map { it.model }
          .map {
              "${it.name} (${it.branches.size} branches, ${it.branches.values.sumBy { it.commits?.size ?: 0 }} commits)"
          }

}


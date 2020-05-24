package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel.ProjectItem

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
                data = first.data merge second.data // This is where projects with different lists of commits replace one another
          )

    private infix fun List<SummaryUiModel>.merge(other: List<SummaryUiModel>): List<SummaryUiModel> =
          (other.reversed() + this.reversed())
                .distinctBy { if (it is ProjectItem) it.model.name else it }
                .reversed()

}
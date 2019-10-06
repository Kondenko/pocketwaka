package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DbModel.
 */
class SummaryResponseConverter : (SummaryRepository.Params, SummaryDbModel, SummaryDbModel) -> SummaryDbModel {

    override fun invoke(param: SummaryRepository.Params, first: SummaryDbModel, second: SummaryDbModel): SummaryDbModel =
            SummaryDbModel(
                    date = first.date,
                    isEmpty = first.isEmpty ?: second.isEmpty ?: false,
                    isAccountEmpty = first.isAccountEmpty ?: second.isAccountEmpty ?: false,
                    isFromCache = first.isFromCache || second.isFromCache,
                    data = first.data + second.data
            )

}


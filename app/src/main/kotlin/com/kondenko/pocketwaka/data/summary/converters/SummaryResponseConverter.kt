package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.utils.date.DateRangeString
import io.reactivex.Maybe

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DbModel.
 */
class SummaryResponseConverter : (SummaryRepository.Params, List<Maybe<SummaryDbModel>>) -> Maybe<SummaryRangeDbModel> {

    override fun invoke(param: SummaryRepository.Params, model: List<Maybe<SummaryDbModel>>): Maybe<SummaryRangeDbModel> =
            Maybe.merge(model)
                    .toList()
                    .toMaybe()
                    .map {
                        SummaryRangeDbModel(
                                range = DateRangeString(param.start, param.end),
                                isFromCache = it.all { it.isFromCache },
                                isEmpty = it.all { it.isEmpty },
                                data = it
                        )
                    }
}


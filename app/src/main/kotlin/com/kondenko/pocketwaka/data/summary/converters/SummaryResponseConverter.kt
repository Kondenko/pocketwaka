package com.kondenko.pocketwaka.data.summary.converters

import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDbModel
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.utils.date.DateRangeString
import io.reactivex.Observable

/**
 * Converts [com.kondenko.pocketwaka.data.summary.service.SummaryService]'s response to a DbModel.
 */
class SummaryResponseConverter : (SummaryRepository.Params, List<Observable<SummaryDbModel>>) -> Observable<SummaryRangeDbModel> {

    override fun invoke(param: SummaryRepository.Params, model: List<Observable<SummaryDbModel>>): Observable<SummaryRangeDbModel> =
            Observable.merge(model)
                    .toList()
                    .map {
                        SummaryRangeDbModel(
                                range = DateRangeString(param.start, param.end),
                                isFromCache = it.all { it.isFromCache },
                                isEmpty = it.all { it.isEmpty },
                                data = it
                        )
                    }
                    .toObservable()
}


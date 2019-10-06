package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Single

class GetDefaultSummaryRange(
        private val dateProvider: DateProvider,
        schedulers: SchedulersContainer
) : UseCaseSingle<Nothing?, DateRange>(schedulers) {

    override fun build(params: Nothing?): Single<DateRange> {
        val today = dateProvider.getToday().time
        return Single.just(DateRange(start = today, end = today))
    }

}
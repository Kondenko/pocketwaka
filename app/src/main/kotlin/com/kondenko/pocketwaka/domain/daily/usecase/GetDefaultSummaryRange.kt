package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.TimeProvider
import io.reactivex.Single

class GetDefaultSummaryRange(
        private val timeProvider: TimeProvider,
        schedulers: SchedulersContainer
) : UseCaseSingle<Nothing?, DateRange>(schedulers) {

    override fun build(params: Nothing?): Single<DateRange> {
        val today = timeProvider.getToday().time
        return Single.just(DateRange(start = today, end = today))
    }

}
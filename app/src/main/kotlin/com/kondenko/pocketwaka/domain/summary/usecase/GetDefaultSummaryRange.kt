package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.screens.summary.SummaryDate
import com.kondenko.pocketwaka.screens.summary.toSummaryDate
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Single

class GetDefaultSummaryRange(
        private val dateProvider: DateProvider,
        schedulers: SchedulersContainer
) : UseCaseSingle<Nothing?, SummaryDate>(schedulers) {

    override fun build(params: Nothing?): Single<SummaryDate> {
        val today = dateProvider.getToday()
        return Single.just(today.toSummaryDate())
    }

}
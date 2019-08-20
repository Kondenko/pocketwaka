package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.daily.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.daily.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.DateRangeString
import com.kondenko.pocketwaka.utils.date.TimeProvider
import io.reactivex.Observable

class GetSummary(
        schedulers: SchedulersContainer,
        private val timeFormatter: TimeProvider,
        private val summaryRepository: SummaryRepository
) : UseCaseObservable<GetSummary.Params, SummaryRangeDto>(schedulers) {

    data class Params(
            val dateRange: DateRange,
            val project: String? = null,
            val branches: String? = null,
            override val refreshRate: Int,
            override val retryAttempts: Int
    ) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = dateRange.end > dateRange.start
    }

    override fun build(params: Params?): Observable<SummaryRangeDto> =
            params?.run {
                val startDate = timeFormatter.getDayAsString(params.dateRange.start)
                val endDate = timeFormatter.getDayAsString(params.dateRange.end)
                summaryRepository.getData(SummaryRepository.Params(DateRangeString(startDate, endDate), project, branches))
            } ?: Observable.error(NullPointerException("Params are null"))

}
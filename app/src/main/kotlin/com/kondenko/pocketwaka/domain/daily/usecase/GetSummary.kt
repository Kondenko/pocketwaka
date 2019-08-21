package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.data.summary.dto.SummaryRangeDto
import com.kondenko.pocketwaka.data.summary.repository.SummaryRepository
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Observable
import java.sql.Date

class GetSummary(
        schedulers: SchedulersContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val dateFormatter: DateFormatter,
        private val summaryRepository: SummaryRepository
) : UseCaseObservable<GetSummary.Params, SummaryRangeDto>(schedulers) {

    data class Params(
            val dateRange: DateRange,
            val project: String? = null,
            val branches: String? = null,
            override val refreshRate: Int,
            override val retryAttempts: Int
    ) : StatefulUseCase.ParamsWrapper(refreshRate, retryAttempts) {
        override fun isValid(): Boolean = dateRange.end >= dateRange.start
    }

    override fun build(params: Params?): Observable<SummaryRangeDto> =
            params?.run {
                val startDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.start))
                val endDate = dateFormatter.formatDateAsParameter(Date(params.dateRange.end))
                getTokenHeader.build().flatMapObservable {tokenHeader ->
                    summaryRepository.getData(SummaryRepository.Params(
                            tokenHeader,
                            startDate,
                            endDate,
                            project,
                            branches
                    ))
                }
            } ?: Observable.error(NullPointerException("Params are null"))

}
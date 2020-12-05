package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.summary.model.AvailableRange
import com.kondenko.pocketwaka.domain.user.HasPremiumFeatures
import com.kondenko.pocketwaka.utils.SchedulersContainer
import com.kondenko.pocketwaka.utils.date.DateProvider
import com.kondenko.pocketwaka.utils.date.DateRange
import io.reactivex.Single

class GetAvailableRange(
      private val schedulersContainer: SchedulersContainer,
      private val hasPremiumFeatures: HasPremiumFeatures,
      private val dateProvider: DateProvider
) : UseCaseSingle<Nothing?, AvailableRange>(schedulersContainer) {

    companion object {
        private const val WEEKS_AVAILABLE_TO_FREE_USERS = 2L
    }

    override fun build(params: Nothing?): Single<AvailableRange> =
          hasPremiumFeatures.build()
                .subscribeOn(schedulersContainer.workerScheduler)
                .observeOn(schedulersContainer.uiScheduler)
                .map { hasPremium ->
                    if (hasPremium) AvailableRange.Unlimited
                    else getDatesAvailableToFreeUsers()
                }
                .onErrorReturnItem(AvailableRange.Unknown)

    private fun getDatesAvailableToFreeUsers() =
          dateProvider.today
                .let { today ->
                    DateRange.Range(today.minusWeeks(WEEKS_AVAILABLE_TO_FREE_USERS).plusDays(1), today)
                }
                .let {
                    AvailableRange.Limited(it)
                }

}
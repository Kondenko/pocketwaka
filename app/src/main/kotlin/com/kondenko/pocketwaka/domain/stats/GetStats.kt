package com.kondenko.pocketwaka.domain.stats

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.data.stats.model.Stats
import com.kondenko.pocketwaka.data.stats.repository.StatsRepository
import com.kondenko.pocketwaka.domain.UseCaseSingle
import com.kondenko.pocketwaka.domain.auth.GetTokenHeaderValue
import com.kondenko.pocketwaka.utils.SchedulerContainer
import javax.inject.Inject

@PerApp
class GetStats
@Inject constructor(
        schedulers: SchedulerContainer,
        private val getTokenHeader: GetTokenHeaderValue,
        private val statsRepository: StatsRepository
) : UseCaseSingle<String, Stats>(schedulers) {

    override fun build(range: String?)
            = getTokenHeader.build()
            .flatMap { header -> statsRepository.getStats(header, range!!) }
            .map { it.stats }

}
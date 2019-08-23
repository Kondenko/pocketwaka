package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.data.summary.model.database.SummaryRangeDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetSummaryState(
        schedulers: SchedulersContainer,
        getSummary: GetSummary,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, List<SummaryDbModel>, SummaryRangeDbModel>(
        schedulers,
        getSummary,
        { dbModelList: List<SummaryDbModel> -> dbModelList.flatMap { it.data } },
        connectivityStatusProvider
)
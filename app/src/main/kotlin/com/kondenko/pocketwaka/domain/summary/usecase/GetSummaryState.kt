package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseCompletable
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.summary.SummaryState
import com.kondenko.pocketwaka.utils.SchedulersContainer

class GetSummaryState(
      schedulers: SchedulersContainer,
      getSummary: UseCaseObservable<GetSummary.Params, SummaryDbModel>,
      clearCache: UseCaseCompletable<Nothing>,
      connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, SummaryDbModel>(
      schedulers = schedulers,
      dataProvider = getSummary,
      clearCache = clearCache,
      connectivityStatusProvider = connectivityStatusProvider
) {

    override fun databaseModelToState(model: SummaryDbModel, isConnected: Boolean): State<List<SummaryUiModel>> {
        return when {
            model.isEmpty == true -> SummaryState.EmptyRange
            model.isAccountEmpty == true -> State.Empty
            else -> super.databaseModelToState(model, isConnected)
        }
    }

}
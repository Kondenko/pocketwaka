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
import com.kondenko.pocketwaka.utils.date.DateProvider

class GetSummaryState(
      getSummary: UseCaseObservable<GetSummary.Params, SummaryDbModel>,
      clearCache: UseCaseCompletable<Nothing>,
      private val shouldShowOnboarding: ShouldShowOnboarding,
      connectivityStatusProvider: ConnectivityStatusProvider,
      private val dateProvider: DateProvider,
      schedulers: SchedulersContainer
) : StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, SummaryDbModel>(
      schedulers = schedulers,
      dataProvider = getSummary,
      clearCache = clearCache,
      connectivityStatusProvider = connectivityStatusProvider
) {

    override fun databaseModelToState(model: SummaryDbModel, isConnected: Boolean): State<List<SummaryUiModel>> =
          when {
              model.isEmpty == true -> SummaryState.EmptyRange
              model.isAccountEmpty == true -> State.Empty
              else -> super.databaseModelToState(model, isConnected).let { addOnboarding(it) }
          }

    private fun addOnboarding(state: State<List<SummaryUiModel>>): State<List<SummaryUiModel>> {
        return when {
            shouldShowOnboarding() && state is State.Success -> {
                state.copy(listOf(SummaryUiModel.Onboarding) + state.data)
            }
            shouldShowOnboarding() && (state is State.Loading && !state.data.isNullOrEmpty()) -> {
                state.copy(listOf(SummaryUiModel.Onboarding) + state.data.orEmpty())
            }
            else -> {
                state
            }
        }
    }

}
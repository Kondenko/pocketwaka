package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.SharedPreferencesRepository
import com.kondenko.pocketwaka.domain.UseCase

class ShouldShowOnboarding(private val sharedPreferencesRepository: SharedPreferencesRepository) : UseCase<Nothing?, Boolean, Boolean> {

    override fun invoke(params: Nothing?, onSuccess: (Boolean) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit): Boolean =
          !sharedPreferencesRepository.hasSeenSummaryOnboarding

}
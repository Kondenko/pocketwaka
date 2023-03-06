package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.SharedPreferencesRepository
import com.kondenko.pocketwaka.domain.UseCase

class SetOnboardingShown(private val sharedPreferencesRepository: SharedPreferencesRepository) : UseCase<Boolean, Nothing, Unit> {

    override fun invoke(isShown: Boolean?, onSuccess: (Nothing) -> Unit, onError: (Throwable) -> Unit, onFinish: () -> Unit) {
        sharedPreferencesRepository.hasSeenSummaryOnboarding = isShown == true
    }

}
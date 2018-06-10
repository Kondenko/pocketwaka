package com.kondenko.pocketwaka.utils

import com.kondenko.pocketwaka.dagger.PerApp
import com.kondenko.pocketwaka.dagger.qualifiers.Ui
import com.kondenko.pocketwaka.dagger.qualifiers.Worker
import io.reactivex.Scheduler
import javax.inject.Inject

/**
 * Holds schedulers for UseCases.
 */
@PerApp
data class SchedulerContainer @Inject constructor(@Ui val uiScheduler: Scheduler, @Worker val workerScheduler: Scheduler)
package com.kondenko.pocketwaka.utils

import io.reactivex.Scheduler

/**
 * Holds schedulers for UseCases.
 */
data class SchedulerContainer(val uiScheduler: Scheduler, val workerScheduler: Scheduler)
package com.kondenko.pocketwaka.testutils

import com.kondenko.pocketwaka.utils.SchedulerContainer
import io.reactivex.schedulers.Schedulers

fun getTestSchedulerContainer() = SchedulerContainer(Schedulers.trampoline(), Schedulers.trampoline())
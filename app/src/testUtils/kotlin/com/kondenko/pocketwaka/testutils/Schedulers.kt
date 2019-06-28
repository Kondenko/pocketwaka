package com.kondenko.pocketwaka.testutils

import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.schedulers.Schedulers

val testSchedulers = SchedulersContainer(Schedulers.trampoline(), Schedulers.trampoline())
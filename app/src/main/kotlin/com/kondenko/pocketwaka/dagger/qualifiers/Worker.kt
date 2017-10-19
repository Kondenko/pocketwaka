package com.kondenko.pocketwaka.dagger.qualifiers

import java.lang.annotation.Documented
import javax.inject.Qualifier

/**
 * A scope for worker Schedulers.
 */
@Qualifier
@MustBeDocumented
annotation class Worker
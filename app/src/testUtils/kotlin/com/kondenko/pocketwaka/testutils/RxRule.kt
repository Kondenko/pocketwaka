package com.kondenko.pocketwaka.testutils


import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Always subscribeOn and observeOn Schedulers.trampoline()
 * for immediate execution.
 */
class RxRule : TestRule {

    private val trampoline = Schedulers.trampoline()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                RxAndroidPlugins.setMainThreadSchedulerHandler { trampoline }
                RxJavaPlugins.setComputationSchedulerHandler { trampoline }
                RxJavaPlugins.setIoSchedulerHandler { trampoline }
                RxJavaPlugins.setNewThreadSchedulerHandler { trampoline }
                try {
                    base.evaluate()
                } finally {
                    RxAndroidPlugins.reset()
                    RxJavaPlugins.reset()
                }
            }
        }
    }

}

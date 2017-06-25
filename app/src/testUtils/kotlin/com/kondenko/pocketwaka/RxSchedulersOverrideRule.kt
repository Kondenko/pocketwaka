package com.kondenko.pocketwaka

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers


/**
 * This rule registers SchedulerHooks for RxJava and RxAndroid to ensure that subscriptions
 * always subscribeOn and observeOn Schedulers.immediate().
 * Warning, this rule will reset RxAndroidPlugins and RxJavaPlugins before and after each test so
 * if the application code uses RxJava plugins this may affect the behaviour of the testing method.
 */
class RxSchedulersOverrideRule : TestRule {

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
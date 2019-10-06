package com.kondenko.pocketwaka.utils.extensions

import io.reactivex.Observable
import org.junit.Test

class RxJavaExtensionsTest {

    @Test
    fun `should start with the specified item`() {
        Observable.just(1, 2, 3)
                .startWithIfNotEmpty(0)
                .test()
                .assertValues(0, 1, 2, 3)
                .assertNoErrors()
    }

    @Test
    fun `should emit nothing`() {
        Observable.empty<Int>()
                .startWithIfNotEmpty(0)
                .test()
                .assertNoValues()
                .assertNoErrors()
    }

}
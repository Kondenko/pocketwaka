package com.kondenko.pocketwaka.utils.extensions

import com.kondenko.pocketwaka.testutils.TestException
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReturnAfterCompletionTest {

    @Test
    fun `should append an item`() {
        Observable.fromArray(1, 2, 3)
              .returnAfterCompletion { lastValue ->
                  assertEquals(3, lastValue)
                  Observable.just(4)
              }
              .testWithLogging()
              .assertValues(1, 2, 3, 4)
              .assertComplete()
    }

    @Test
    fun `should append an item to an empty observable`() {
        Observable.empty<Int>()
              .returnAfterCompletion { lastValue ->
                  assertNull(lastValue)
                  Observable.just(1)
              }
              .testWithLogging()
              .assertValues(1)
              .assertComplete()
    }

    @Test
    fun `should NOT append an item in case of an error`() {
        Observable.error<Int>(TestException())
              .returnAfterCompletion { Observable.just(1) }
              .testWithLogging()
              .assertNoValues()
              .assertTerminated()
    }

}
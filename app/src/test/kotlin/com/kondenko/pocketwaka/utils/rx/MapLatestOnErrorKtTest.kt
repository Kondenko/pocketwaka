package com.kondenko.pocketwaka.utils.rx

import com.kondenko.pocketwaka.testutils.TestException
import com.kondenko.pocketwaka.utils.WakaLog
import io.reactivex.Observable
import org.junit.Test

class MapLatestOnErrorKtTest {


    @Test
    fun `should emit last item and throwable`() {

        data class Value(val index: Int, val isResultOfException: Boolean)

        val lastValueBeforeException = 4
        Observable.fromArray(1, 2, 3, lastValueBeforeException, 5)
              .map { Value(it, isResultOfException = false) }
              .doOnNext { if (it.index == lastValueBeforeException) throw TestException() }
              .mapLatestOnError { i, throwable -> i!!.copy(isResultOfException = throwable != null) }
              .test()
              .assertValues(
                    Value(1, false),
                    Value(2, false),
                    Value(3, false),
                    Value(3, true)
              )
              .assertNoErrors()
              .assertNotComplete()
    }

    @Test
    fun `should complete if an exception happens right away`() {
        Observable.error<Int>(TestException())
              .mapLatestOnError { i, throwable -> i }
              .test()
              .assertNoValues()
              .assertError(TestException::class.java)
              .assertTerminated()
    }

    @Test
    fun `should return default item if it's present and an exception happens right away`() {
        Observable.error<Int>(TestException())
              .mapLatestOnError(0) { i, throwable -> i }
              .test()
              .assertValue(0)
              .assertNoErrors()
              .assertComplete()
    }

    @Test
    fun `should not fail if no values are present`() {
        Observable.never<Int>()
              .mapLatestOnError { i, throwable -> i!!.let { it * 2 } }
              .test()
              .assertNoValues()
              .assertNoErrors()
              .assertNotComplete()
    }

    @Test
    fun `should reemit an exception and complete`() {
        val lastValueBeforeException = 4
        val regularException = TestException("Regular exception")
        val exceptionalException = TestException("Exceptional exception")
        Observable.fromArray(1, 2, 3, lastValueBeforeException, 5)
              .doOnNext { if (it == lastValueBeforeException) throw regularException }
              .mapLatestOnError { i, throwable ->
                  if (throwable == null) i.also { WakaLog.d("Mapping $it") }
                  else throw exceptionalException.also { WakaLog.d("Throwing $it") }
              }
              .test()
              .assertValues(1, 2, 3)
              .assertError(exceptionalException)
              .assertTerminated()
    }

}
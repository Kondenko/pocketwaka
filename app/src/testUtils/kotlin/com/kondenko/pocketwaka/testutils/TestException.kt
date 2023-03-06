package com.kondenko.pocketwaka.testutils

data class TestException(override val message: String? = null) : Throwable(message)
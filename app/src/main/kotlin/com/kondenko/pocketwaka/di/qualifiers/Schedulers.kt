package com.kondenko.pocketwaka.di.qualifiers

import org.koin.core.qualifier.StringQualifier

object Scheduler {
    val Worker = StringQualifier("worker")
    val Ui = StringQualifier("ui")
}

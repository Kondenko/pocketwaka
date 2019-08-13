package com.kondenko.pocketwaka.domain.daily.model

import androidx.annotation.IntRange

sealed class SummaryUiModel {

    sealed class Status(val lastUpdated: Long? = null) {
        class Loading(lastUpdated: Long? = null) : Status(lastUpdated)
        class Offline(lastUpdated: Long? = null) : Status(lastUpdated)
    }

    data class TimeTracked(val time: String, @IntRange(from = -100, to = 100) val percentDelta: Int)
        : SummaryUiModel()

    object ProjectsBlock

    data class Projects(val projects: List<Project>)

}

data class Project(val name: String, val timeTracked: String, val isRepoConnected: Boolean, val branches: List<Branch>)

data class Branch(val name: String, val timeTracked: String, val commits: List<Commit>)

data class Commit(val message: String, val timeTracked: String)
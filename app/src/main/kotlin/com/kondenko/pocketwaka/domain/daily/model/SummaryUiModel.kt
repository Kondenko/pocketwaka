package com.kondenko.pocketwaka.domain.daily.model

import androidx.annotation.IntRange

sealed class SummaryUiModel {

    sealed class Status(val lastUpdated: Long? = null) : SummaryUiModel() {
        class Loading(lastUpdated: Long? = null) : Status(lastUpdated)
        class Offline(lastUpdated: Long? = null) : Status(lastUpdated)
    }

    data class TimeTracked(val time: String, @IntRange(from = -100, to = 100) val percentDelta: Int?)
        : SummaryUiModel()

    object ProjectsTitle
        : SummaryUiModel()

    data class Project(val models: List<ProjectModel>) : SummaryUiModel()

}

sealed class ProjectModel {
    data class ProjectName(val name: String, val timeTracked: String?) : ProjectModel()
    data class Branch(val name: String, val timeTracked: String?) : ProjectModel()
    data class Commit(val message: String, val timeTracked: String?) : ProjectModel()
    data class ConnectRepoAction(val url: String) : ProjectModel()
    object MoreCommitsAction : ProjectModel()
    object NoCommitsLabel : ProjectModel()
}


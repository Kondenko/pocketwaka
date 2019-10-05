package com.kondenko.pocketwaka.domain.daily.model

import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.StatusMarker

sealed class SummaryUiModel {

    data class Status(override val status: ScreenStatus) : SummaryUiModel(), StatusMarker

    data class TimeTracked(val time: String, val percentDelta: Int?) : SummaryUiModel()

    object ProjectsTitle : SummaryUiModel()

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


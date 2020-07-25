package com.kondenko.pocketwaka.domain.summary.model

import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.StatusMarker

sealed class SummaryUiModel {

    data class Status(override val status: ScreenStatus) : SummaryUiModel(), StatusMarker

    data class TimeTracked(val time: String, val percentDelta: Int?) : SummaryUiModel()

    object ProjectsTitle : SummaryUiModel()

    data class ProjectItem(val model: Project) : SummaryUiModel()

}
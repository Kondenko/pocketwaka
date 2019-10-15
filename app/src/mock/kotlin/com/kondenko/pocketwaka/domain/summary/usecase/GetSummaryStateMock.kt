package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.summary.model.ProjectModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.Observable

class GetSummaryStateMock(private val getSummaryState: GetSummaryState) :
      UseCase<GetSummary.Params, State<List<SummaryUiModel>>, Observable<State<List<SummaryUiModel>>>> by getSummaryState {

    private val mockModels = listOf(
            SummaryUiModel.TimeTracked("11 hrs 55 mins", -71),
            SummaryUiModel.ProjectsTitle,
            SummaryUiModel.Project(
                    listOf(
                            ProjectModel.ProjectName("Ridiculously long project name no one will read", "57m"),
                            ProjectModel.Branch("branch", "1h 12m"),
                            ProjectModel.NoCommitsLabel,
                            ProjectModel.Branch("this branch name is too long to fit into the TextView", "5h 16m"),
                            ProjectModel.Commit("First commit", "3m"),
                            ProjectModel.Commit("This commit message is too long to fit into the TextView", "1h 3m")
                    )
            ),
            SummaryUiModel.Project(
                    listOf(
                            ProjectModel.ProjectName("PocketWaka", "10h 5m"),
                            ProjectModel.Branch("branch", "1h 12m"),
                            ProjectModel.Branch("branch", "1h 12m"),
                            ProjectModel.Branch("branch", "1h 12m"),
                            ProjectModel.ConnectRepoAction("")
                    )
            ),
            SummaryUiModel.Project(
                    listOf(
                            ProjectModel.ProjectName("PlaygroundProject", "57m")
                    )
            )
    )

}
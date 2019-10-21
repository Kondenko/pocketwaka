package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.summary.model.ProjectModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.Observable

class GetSummaryStateMock(private val getSummaryState: GetSummaryState) :
      UseCase<GetSummary.Params, State<List<SummaryUiModel>>, Observable<State<List<SummaryUiModel>>>> by getSummaryState {

    private val mockModels = listOf(
          SummaryUiModel.TimeTracked("8 hrs 12 mins", 15),
          SummaryUiModel.ProjectsTitle,
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("PocketWaka", "8h"),
                      ProjectModel.Branch("summaries", "7h"),
                      ProjectModel.Commit("Clear summary cache on logout", "1h 3m"),
                      ProjectModel.Commit("Add caching for summaries", "2h 40m"),
                      ProjectModel.Commit("Introduce BrowserWindow to open auth and repo pages on the web", "2h 17m"),
                      ProjectModel.Commit("Update summary skeleton layouts", "1h"),
                      ProjectModel.Branch("develop", "1h"),
                      ProjectModel.NoCommitsLabel
                )
          ),
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("Personal website", "5m"),
                      ProjectModel.ConnectRepoAction("https://wakatime.com/projects/plugin-samples/edit")
                )
          ),
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("SafelyDeleteComponents", "7m")
                )
          )
    )

    override fun build(params: GetSummary.Params?): Observable<State<List<SummaryUiModel>>> {
        return Observable.just(State.Success(mockModels))
    }
}
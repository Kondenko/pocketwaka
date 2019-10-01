package com.kondenko.pocketwaka.domain.daily.usecase

import com.kondenko.pocketwaka.data.android.ConnectivityStatusProvider
import com.kondenko.pocketwaka.data.summary.model.database.SummaryDbModel
import com.kondenko.pocketwaka.domain.StatefulUseCase
import com.kondenko.pocketwaka.domain.UseCaseObservable
import com.kondenko.pocketwaka.domain.daily.model.ProjectModel
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.utils.SchedulersContainer
import io.reactivex.Observable

class GetSummaryStateMock(
        schedulers: SchedulersContainer,
        useCase: UseCaseObservable<GetSummary.Params, SummaryDbModel>,
        connectivityStatusProvider: ConnectivityStatusProvider
) : StatefulUseCase<GetSummary.Params, List<SummaryUiModel>, SummaryDbModel>(schedulers, useCase, connectivityStatusProvider) {

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

    override fun build(params: GetSummary.Params?): Observable<State<List<SummaryUiModel>>> =
            Observable.just<State<List<SummaryUiModel>>>(State.Empty)

}
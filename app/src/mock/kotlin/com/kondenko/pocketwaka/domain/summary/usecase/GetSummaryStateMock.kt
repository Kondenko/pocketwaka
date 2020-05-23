package com.kondenko.pocketwaka.domain.summary.usecase

import com.kondenko.pocketwaka.data.android.DateFormatter
import com.kondenko.pocketwaka.domain.UseCase
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.State
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class GetSummaryStateMock(private val getSummaryState: GetSummaryState, private val dateFormatter: DateFormatter) :
      UseCase<GetSummary.Params, State<List<SummaryUiModel>>, Observable<State<List<SummaryUiModel>>>> by getSummaryState {

/*
    private val mockModels = listOf(
          SummaryUiModel.TimeTracked(time(8, 12, DateFormatter.Format.Long), 15),
          SummaryUiModel.ProjectsTitle,
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("PocketWaka",
                            time(8)
                      ),
                      ProjectModel.Branch("summaries",
                            time(7)
                      ),
                      ProjectModel.Commit("Clear summary cache on logout",
                            time(1, 3)
                      ),
                      ProjectModel.Commit("Add caching for summaries",
                            time(2, 40)
                      ),
                      ProjectModel.Commit("Introduce BrowserWindow to open auth and repo pages on the web",
                            time(2, 17)
                      ),
                      ProjectModel.Commit("Update summary skeleton layouts",
                            time(1)
                      ),
                      ProjectModel.Branch("develop",
                            time(1)
                      ),
                      ProjectModel.NoCommitsLabel
                )
          ),
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("Personal website",
                            time(0, 5)
                      ),
                      ProjectModel.ConnectRepoAction("https://wakatime.com/projects/plugin-samples/edit")
                )
          ),
          SummaryUiModel.Project(
                listOf(
                      ProjectModel.ProjectName("SafelyDeleteComponents", time(0, 7))
                )
          )
    )
*/

    private fun time(h: Int, m: Int = 0, format: DateFormatter.Format = DateFormatter.Format.Short) =
          dateFormatter.toHumanReadableTime(h, m, format)

    override fun build(params: GetSummary.Params?): Observable<State<List<SummaryUiModel>>> {
        return concatWithDelay(
              (State.Failure.Unknown<Nothing>(isFatal = true))
        )
    }

    private fun <T> concatWithDelay(vararg items: T) = Observable.concat(items.mapIndexed { index, item ->
        Observable.just(item).apply {
            if (index > 0) {
                delay(3, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
            }
        }
    })

}
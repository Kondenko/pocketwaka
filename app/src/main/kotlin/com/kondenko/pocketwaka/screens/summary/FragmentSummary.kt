package com.kondenko.pocketwaka.screens.summary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.summary.model.ProjectModel
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.base.BaseFragment
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.toListOrEmpty
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.android.synthetic.main.fragment_summary.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentSummary : BaseFragment<SummaryUiModel, List<SummaryUiModel>, SummaryAdapter, SummaryState>(), Refreshable {

    private val vm: SummaryViewModel by viewModel()

    private val browserWindow: BrowserWindow by inject { parametersOf(context, viewLifecycleOwner) }

    override val containerId: Int = R.id.framelayout_summary_root

    override val stateFragment = SummaryStateFragment()

    private val projectSkeleton = listOf(
            ProjectModel.ProjectName("", null),
            ProjectModel.Commit("", null),
            ProjectModel.Commit("", null),
            ProjectModel.Commit("", null)
    ).let(SummaryUiModel::Project)

    private val skeletonItems = listOf(
            SummaryUiModel.TimeTracked("", 1),
            SummaryUiModel.ProjectsTitle,
            projectSkeleton,
            projectSkeleton
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList(view)
        vm.state().observe(viewLifecycleOwner) {
            WakaLog.d("New summary state: $it")
            when (it) {
                is SummaryState.EmptyRange -> {
                    showData(false)
                    stateFragment.setState(it)
                }
                is SummaryState.ConnectRepo -> {
                    connectRepo(it.url)
                }
                else -> it.render()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        vm.onResume()
    }

    override fun provideDataView(): View = recyclerview_summary

    private fun setupList(view: View) {
        with(view.recyclerview_summary) {
            listSkeleton = currentScope.get { parametersOf(this, context, skeletonItems) }
            adapter = listSkeleton.actualAdapter.apply {
                connectRepoClicks().subscribeBy(
                      onNext = vm::connectRepoClicked,
                      onError = WakaLog::w
                )
            }
        }
    }

    private fun connectRepo(url: String) {
        browserWindow.openUrl(url)
    }

    override fun updateData(data: List<SummaryUiModel>?, status: ScreenStatus?) {
        recyclerview_summary.apply {
            val statusModel = status?.let(SummaryUiModel::Status).toListOrEmpty()
            data?.let { listSkeleton.actualAdapter.items = statusModel + it }
        }
    }

    override fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            reloadScreen()
        }
    }

    override fun reloadScreen() {
        vm.getSummaryForRange()
    }

}

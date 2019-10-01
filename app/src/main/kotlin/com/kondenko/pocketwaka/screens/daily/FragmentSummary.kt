package com.kondenko.pocketwaka.screens.daily

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.daily.model.ProjectModel
import com.kondenko.pocketwaka.domain.daily.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.base.BaseFragment
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.toListOrEmpty
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.android.synthetic.main.fragment_summary.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentSummary : BaseFragment<SummaryUiModel, List<SummaryUiModel>, SummaryAdapter, SummaryState>() {

    private val vm: SummaryViewModel by viewModel()

    private val browserWindow: BrowserWindow by inject { parametersOf(requireContext(), viewLifecycleOwner) }

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

    override fun provideDataView(): View = recyclerview_summary

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList(view)
        vm.state.observe(viewLifecycleOwner) {
            Timber.d("New summary state: $it")
            when (it) {
                is SummaryState.EmptyRange -> {
                    showData(false)
                    stateFragment.setState(it)
                }
                else -> it.render()
            }
        }
    }

    private fun setupList(view: View) {
        with(view.recyclerview_summary) {
            listSkeleton = currentScope.get { parametersOf(this, context, skeletonItems) }
            adapter = listSkeleton.actualAdapter.apply {
                connectRepoClicks().subscribeBy(onNext = ::connectRepo, onError = Timber::w)
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

    override fun reloadScreen() {
        vm.getSummaryForRange()
    }

}

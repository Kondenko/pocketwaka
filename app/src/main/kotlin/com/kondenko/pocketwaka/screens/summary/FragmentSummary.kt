package com.kondenko.pocketwaka.screens.summary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.observe
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.domain.summary.model.Branch
import com.kondenko.pocketwaka.domain.summary.model.Commit
import com.kondenko.pocketwaka.domain.summary.model.Project
import com.kondenko.pocketwaka.domain.summary.model.SummaryUiModel
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseFragment
import com.kondenko.pocketwaka.utils.BrowserWindow
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.extensions.toListOrEmpty
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.android.synthetic.main.fragment_summary.view.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FragmentSummary :
    BaseFragment<SummaryUiModel, List<SummaryUiModel>, SummaryAdapter, SummaryState>() {

    companion object {

        private const val KEY_DATE = "date"

        fun create(date: DateRange) = FragmentSummary().apply {
            arguments = bundleOf(KEY_DATE to date)
        }

    }

    private val date: DateRange by lazy {
        requireArguments().getParcelable<DateRange>(KEY_DATE)
            ?: throw IllegalArgumentException("You must pass a DateRange to this fragment")
    }

    private val vm: SummaryViewModel by viewModel { parametersOf(date) }

    private val eventTracker: EventTracker by inject()

    private val browserWindow: BrowserWindow by inject { parametersOf(context, viewLifecycleOwner) }

    override val containerId: Int = R.id.framelayout_summary_root

    override val stateFragment = SummaryStateFragment()

    private val projectSkeleton = Project(
        name = "",
        totalSeconds = 0,
        isRepoConnected = true,
        repositoryUrl = "",
        branches = mapOf(
            "" to Branch("", 0, (0..2).map { Commit("", "", 0) })
        )
    ).let(SummaryUiModel::ProjectItem)

    private val skeletonItems: List<SummaryUiModel> = listOf(
        SummaryUiModel.TimeTracked("", 1),
        SummaryUiModel.ProjectsTitle,
        projectSkeleton,
        projectSkeleton,
        projectSkeleton
    )

    override fun getDataView(): RecyclerView = recyclerview_summary

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listSkeleton = get { parametersOf(context, skeletonItems) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList(view)
        loadData()
    }

    override fun onResume() {
        super.onResume()
        vm.updateDataIfRepoHasBeenConnected()
    }

    private fun setupList(view: View) = with(view.recyclerview_summary) {
        itemAnimator = null
        listSkeleton.actualAdapter.apply {
            dismissOnboardingClicks()
                .subscribeBy(
                    onNext = { vm.dismissOnboarding() },
                    onError = WakaLog::w
                )
            connectRepoClicks()
                .doOnNext { eventTracker.log(Event.Summary.ConnectRepoClicks) }
                .subscribeBy(
                    onNext = vm::connectRepoClicked,
                    onError = WakaLog::w
                )
        }
    }

    private fun loadData() {
        vm.state().observe(viewLifecycleOwner) {
            WakaLog.d("New summary state: $it")
            if (it is State.Empty) {
                eventTracker.log(Event.EmptyState.Account(Screen.Summary))
            }
            when (it) {
                is SummaryState.EmptyRange -> {
                    eventTracker.log(Event.EmptyState.Screen(Screen.Summary))
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

    private fun connectRepo(url: String) {
        browserWindow.openUrl(url)
    }

    override fun updateData(data: List<SummaryUiModel>?, status: ScreenStatus?) {
        val dateIsToday = (date as? DateRange.SingleDay) == DateRange.PredefinedRange.Today.range
        val data = data?.filter { it !is SummaryUiModel.Onboarding || dateIsToday }
        val statusModel = status?.let(SummaryUiModel::Status).toListOrEmpty()
        data?.let {
            listSkeleton.actualAdapter.items = statusModel + it
        }
    }

    override fun reloadScreen() {
        vm.fetchSummary()
    }

}

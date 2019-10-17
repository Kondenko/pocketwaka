package com.kondenko.pocketwaka.screens.stats


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.postDelayed
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Event
import com.kondenko.pocketwaka.analytics.EventTracker
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import com.kondenko.pocketwaka.domain.stats.model.StatsUiModel
import com.kondenko.pocketwaka.screens.Refreshable
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseFragment
import com.kondenko.pocketwaka.screens.stats.adapter.StatsAdapter
import com.kondenko.pocketwaka.screens.stats.model.ScrollDirection
import com.kondenko.pocketwaka.utils.extensions.dp
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.times
import com.kondenko.pocketwaka.utils.extensions.toListOrEmpty
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_stats.*
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentStatsTab : BaseFragment<StatsUiModel, List<StatsUiModel>, StatsAdapter, State<List<StatsUiModel>>>(), Refreshable {

    companion object Args {
        const val argRange = "range"
    }

    private val range: String? by lazy { arguments?.getString(argRange) }

    private lateinit var vm: StatsViewModel

    private val eventTracker: EventTracker by inject()

    override val containerId: Int = R.id.framelayout_stats_range_root

    private val skeletonStatsCard = mutableListOf(StatsItem("", null, null, null)) * 3

    private val skeletonItems = listOf(
          StatsUiModel.Info(null, null),
          StatsUiModel.BestDay("", "", 0),
          StatsUiModel.Stats("", skeletonStatsCard),
          StatsUiModel.Stats("", skeletonStatsCard)
    )

    private val scrollDirection = BehaviorSubject.create<ScrollDirection>()

    private var shadowAnimationNeeded = true

    /**
     * The minimum value the RecyclerView has to be scrolled for
     * for the AppBar shadow to be shown
     */
    private val minScrollOffset: Float by lazy {
        val value = 8f
        context?.dp(value) ?: value
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        vm = getViewModel {
            parametersOf(range)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.postDelayed(50) {
            setupUi(view.context)
            vm.state().observe(viewLifecycleOwner) {
                Timber.d("New stats state (${range}): $it")
                if (it is State.Empty) {
                    eventTracker.log(Event.EmptyState.Account(Screen.Stats.Tab(range)))
                }
                it.render()
            }
        }
    }

    override fun provideDataView(): View = recyclerview_stats

    private fun setupUi(context: Context) {
        with(recyclerview_stats) {
            listSkeleton = currentScope.get { parametersOf(this@with, context, skeletonItems) }
            adapter = listSkeleton.skeletonAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateAppBarElevation()
                }
            })
            showData(true)
        }
    }

    override fun updateData(data: List<StatsUiModel>?, status: ScreenStatus?) {
        recyclerview_stats.apply {
            val statusModel = status?.let(StatsUiModel::Status).toListOrEmpty()
            data?.let { listSkeleton.actualAdapter.items = statusModel + it }
        }
    }

    private fun updateAppBarElevation() {
        shadowAnimationNeeded = if (recyclerview_stats.computeVerticalScrollOffset() >= minScrollOffset) {
            if (shadowAnimationNeeded) scrollDirection.onNext(ScrollDirection.Down)
            false
        } else {
            scrollDirection.onNext(ScrollDirection.Up)
            true
        }
    }

    override fun reloadScreen() = vm.update()

    fun scrollToTop() = recyclerview_stats.smoothScrollToPosition(0)

    fun scrollDirection(): Observable<ScrollDirection> = scrollDirection

    override fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            vm.update()
        }
    }

}

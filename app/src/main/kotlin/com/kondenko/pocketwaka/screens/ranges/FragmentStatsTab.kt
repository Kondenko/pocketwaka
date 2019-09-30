package com.kondenko.pocketwaka.screens.ranges


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.ranges.model.StatsItem
import com.kondenko.pocketwaka.domain.ranges.model.StatsUiModel
import com.kondenko.pocketwaka.screens.ScreenStatus
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.base.BaseFragment
import com.kondenko.pocketwaka.screens.ranges.adapter.StatsAdapter
import com.kondenko.pocketwaka.screens.ranges.model.ScrollDirection
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.times
import com.kondenko.pocketwaka.utils.extensions.toListOrEmpty
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_stats_range.*
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentStatsTab : BaseFragment<StatsUiModel, List<StatsUiModel>, StatsAdapter, State<List<StatsUiModel>>>() {

    companion object {
        const val ARG_RANGE = "range"
    }

    private val vm: RangesViewModel by viewModel {
        parametersOf(arguments?.getString(ARG_RANGE))
    }

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
        context?.adjustForDensity(value) ?: value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats_range, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view.context)
        vm.state.observe(viewLifecycleOwner) {
            Timber.d("New stats state: $it")
            it.render()
        }
    }

    override fun provideDataView(): View = stats_range_recyclerview

    private fun setupUi(context: Context) {
        with(stats_range_recyclerview) {
            listSkeleton = currentScope.get { parametersOf(this@with, context, skeletonItems) }
            layoutManager = LinearLayoutManager(context)
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
        stats_range_recyclerview.apply {
            val statusModel = status?.let(StatsUiModel::Status).toListOrEmpty()
            data?.let { listSkeleton.actualAdapter.items = statusModel + it }
        }
    }

    private fun updateAppBarElevation() {
        shadowAnimationNeeded = if (stats_range_recyclerview.computeVerticalScrollOffset() >= minScrollOffset) {
            if (shadowAnimationNeeded) scrollDirection.onNext(ScrollDirection.Down)
            false
        } else {
            scrollDirection.onNext(ScrollDirection.Up)
            true
        }
    }

    override fun reloadScreen() = vm.update()

    fun scrollDirection(): Observable<ScrollDirection> = scrollDirection

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            vm.update()
        }
    }

}

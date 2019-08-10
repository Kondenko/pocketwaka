package com.kondenko.pocketwaka.screens.ranges


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.ranges.model.StatsItem
import com.kondenko.pocketwaka.domain.ranges.model.StatsModel
import com.kondenko.pocketwaka.screens.State
import com.kondenko.pocketwaka.screens.StateFragment
import com.kondenko.pocketwaka.screens.ranges.adapter.StatsAdapter
import com.kondenko.pocketwaka.screens.ranges.model.ScrollDirection
import com.kondenko.pocketwaka.ui.skeleton.RecyclerViewSkeleton
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.times
import com.kondenko.pocketwaka.utils.extensions.transaction
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_stats_range.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class FragmentStatsTab : Fragment() {

    companion object {
        const val ARG_RANGE = "range"
    }

    private lateinit var vm: StatsViewModel

    /**
     * The minimum value the RecyclerView has to be scrolled for
     * for the AppBar shadow to be shown
     */
    private val minScrollOffset: Float by lazy {
        val value = 8f
        context?.adjustForDensity(value) ?: value
    }

    private var shadowAnimationNeeded = true

    private val scrollDirection = BehaviorSubject.create<ScrollDirection>()

    private lateinit var listSkeleton: RecyclerViewSkeleton<StatsModel, StatsAdapter>

    private lateinit var statsAdapter: StatsAdapter

    private val skeletonStatsCard = mutableListOf(StatsItem("", null, null, null)) * 3
    private val skeletonItems = listOf(
            StatsModel.Info(null, null),
            StatsModel.BestDay("", "", 0),
            StatsModel.Stats("", skeletonStatsCard),
            StatsModel.Stats("", skeletonStatsCard)
    )

    private val fragmentState: StateFragment by lazy {
        val fragment = StateFragment()
        childFragmentManager.transaction {
            add(fragment, null)
        }
        fragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        vm = getViewModel { parametersOf(arguments?.getString(ARG_RANGE)) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats_range, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view.context)
        vm.state().observe(viewLifecycleOwner, Observer {
            render(it)
        })
    }

    private fun setupUi(context: Context) {
        with(stats_range_recyclerview) {
            listSkeleton = RecyclerViewSkeleton(
                    recyclerView = this,
                    actualAdapter = StatsAdapter(context, false),
                    skeletonAdapter = StatsAdapter(context, true),
                    skeletonItems = skeletonItems
            )
            statsAdapter = listSkeleton.actualAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateAppBarElevation()
                }
            })
        }
        showData(true)
    }

    private fun render(state: State<List<StatsModel>>) {
        listSkeleton.show((state as? State.Loading<*>)?.isInterrupting == true)
        when (state) {
            is State.Success -> state.render()
            is State.Loading -> state.render()
            is State.Offline -> state.render()
            is State.Empty -> state.render()
            is State.Failure -> state.render()
        }
    }

    private fun State.Loading<List<StatsModel>>.render() {
        showData(true)
        if (!isInterrupting) {
            updateStats(listOf(StatsModel.Status.Loading()) + (data ?: emptyList()))
        }
    }

    private fun State.Success<List<StatsModel>>.render() {
        showData(true)
        updateStats(data)
    }

    private fun State.Failure<List<StatsModel>>.render() {
        exception?.report()
        showData(!isFatal)
        if (isFatal) {
            fragmentState.setState(this, onActionClick = vm::update)
        } else {
            updateStats(data)
            view?.let {
                val errorRes = when (this) {
                    is State.Failure.Unknown -> R.string.stats_error_unknown
                    is State.Failure.UnknownRange -> R.string.stats_error_unknown_range
                    is State.Failure.NoNetwork -> R.string.stats_error_unknown_range_no_network
                }
                Snackbar.make(it, errorRes, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun State.Offline<List<StatsModel>>.render() {
        showData(data != null)
        if (data == null) {
            fragmentState.setState(this)
        } else {
            updateStats(listOf(StatsModel.Status.Offline()) + data)
        }
    }

    private fun State.Empty.render() {
        showData(false)
        fragmentState.setState(this, ::openPlugins)
    }

    private fun updateStats(data: List<StatsModel>?) = stats_range_recyclerview.apply {
        data?.let { statsAdapter.items = it }
    }

    private fun showData(show: Boolean) {
        stats_range_recyclerview.isVisible = show
        fragmentState.let {
            childFragmentManager.transaction {
                if (show) hide(it)
                else show(it)
            }
        }
    }

    private fun openPlugins() {
        val uri = Const.URL_PLUGINS
        val builder = CustomTabsIntent.Builder()
        builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary_light))
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(uri))
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

    fun scrollDirection(): Observable<ScrollDirection> = scrollDirection

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            vm.update()
        }
    }

}

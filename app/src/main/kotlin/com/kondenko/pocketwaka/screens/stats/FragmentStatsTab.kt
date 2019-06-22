package com.kondenko.pocketwaka.screens.stats


import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.utils.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.rxClicks
import com.kondenko.pocketwaka.utils.extensions.showFirstView
import com.kondenko.pocketwaka.utils.report
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.layout_stats_empty.*
import kotlinx.android.synthetic.main.layout_stats_error.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class FragmentStatsTab : Fragment() {

    companion object {
        const val ARG_RANGE = "range"
    }

    private val vm: StatsViewModel by viewModel { parametersOf(arguments?.getString(ARG_RANGE)) }

    private var shadowAnimationNeeded = true

    private val scrollDirection = BehaviorSubject.create<ScrollDirection>()

    private var statsAdapter: StatsAdapter? = null

    private var skeletonAdapter: StatsAdapter? = null

    private var recyclerViewStats: RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            statsAdapter = StatsAdapter(it)
            skeletonAdapter = StatsAdapter(it, true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view)
        vm.state().observe(viewLifecycleOwner, Observer { state ->
            Timber.d("${arguments?.get(ARG_RANGE)} state updated: $state")
            when (state) {
                is State.Success<List<StatsModel>> -> onSuccess(state.data)
                is State.Failure<List<StatsModel>> -> onError(state)
                is State.Loading<List<StatsModel>> -> onLoading(state.skeletonData)
                State.Empty -> onEmpty()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateAppBarElevation()
    }

    private fun setupUi(view: View) {

        view.button_errorstate_retry.rxClicks()
                .subscribe { vm.update() }
                .attachToLifecycle(viewLifecycleOwner)

        with(layout_data as RecyclerView) {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateAppBarElevation()
                }
            })
            recyclerViewStats = this
        }

        button_emptystate_plugins.rxClicks().subscribe {
            val uri = Const.URL_PLUGINS
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(uri))
        }.attachToLifecycle(viewLifecycleOwner)
    }

    private fun onSuccess(model: List<StatsModel>) {
        showFirstView(layout_data, layout_empty, layout_error)
        statsAdapter?.items = model
        recyclerViewStats?.adapter = statsAdapter
    }

    private fun onLoading(skeletonModel: List<StatsModel>) {
        showFirstView(layout_data, layout_empty, layout_error)
        skeletonAdapter?.let {
            if (it.items.isEmpty()) it.items = skeletonModel
            recyclerViewStats?.adapter = it
        }
    }

    private fun onEmpty() {
        showFirstView(layout_empty, layout_data, layout_error)
    }

    private fun onError(error: State.Failure<*>) {
        @Suppress("WhenWithOnlyElse") // will be extended in the future
        when (error) {
            else -> {
                showFirstView(layout_error, layout_empty, layout_data)
            }
        }
        error.exception?.report()
    }

    private fun updateAppBarElevation() {
        shadowAnimationNeeded = if ((layout_data as RecyclerView).computeVerticalScrollOffset() >= 10) {
            if (shadowAnimationNeeded) {
                scrollDirection.onNext(ScrollDirection.Down)
            }
            false
        } else {
            scrollDirection.onNext(ScrollDirection.Up)
            true
        }
    }

    fun scrollDirection(): Observable<ScrollDirection> = scrollDirection

    fun isScrollviewOnTop() = layout_data?.scrollY ?: 0 == 0

    fun subscribeToRefreshEvents(refreshEvents: Observable<Any>): Disposable {
        return refreshEvents.subscribe {
            vm.update()
        }
    }

}

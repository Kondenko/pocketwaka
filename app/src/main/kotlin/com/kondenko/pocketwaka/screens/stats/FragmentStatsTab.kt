package com.kondenko.pocketwaka.screens.stats


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsModel
import com.kondenko.pocketwaka.screens.base.ErrorType
import com.kondenko.pocketwaka.screens.base.State
import com.kondenko.pocketwaka.ui.Skeleton
import com.kondenko.pocketwaka.utils.attachToLifecycle
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.rxClicks
import com.kondenko.pocketwaka.utils.extensions.showFirstView
import com.kondenko.pocketwaka.utils.negateIfTrue
import com.kondenko.pocketwaka.utils.report
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_stats.*
import kotlinx.android.synthetic.main.layout_stats_empty.*
import kotlinx.android.synthetic.main.layout_stats_error.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class FragmentStatsTab : Fragment() {

    companion object {
        const val ARG_RANGE = "range"
    }

    private val vm: StatsViewModel by viewModel { parametersOf(arguments?.getString(ARG_RANGE)) }

    private var shadowAnimationNeeded = true

    private val scrollDirection = PublishSubject.create<ScrollDirection>()

    private lateinit var skeleton: Skeleton

    private var statsAdapter: StatsAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        statsAdapter = context?.let { StatsAdapter(context) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view)
        vm.state().observe(viewLifecycleOwner) { state ->
            when (state) {
                is State.Success -> onSuccess(state.data)
                is State.Failure -> onError(state.errorType)
                is State.Loading -> onSuccess(state.skeletonData, true)
                State.Empty -> onEmpty()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateAppBarElevation()
    }

    private fun setupUi(view: View) {

        view.button_errorstate_retry.rxClicks().subscribe {
            vm.update()
        }.attachToLifecycle(viewLifecycleOwner)

        with(layout_data as RecyclerView) {
            adapter = statsAdapter
            layoutManager = LinearLayoutManager(context)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    updateAppBarElevation()
                }
            })
        }

        button_emptystate_plugins.rxClicks().subscribe {
            val uri = Const.URL_PLUGINS
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(uri))
        }.attachToLifecycle(viewLifecycleOwner)

        setupSkeleton()
    }

    private fun onSuccess(model: List<StatsModel>, isSkeleton: Boolean = false) {
        showFirstView(layout_data, layout_empty, layout_error)
        if (!isSkeleton) skeleton.hide()
        skeleton.refreshViews()
        statsAdapter?.items = model
        layout_data.post {
            if (isSkeleton) skeleton.show()
        }
    }

    private fun onEmpty() {
        showFirstView(layout_empty, layout_data, layout_error)
    }

    private fun onError(error: ErrorType) {
        @Suppress("WhenWithOnlyElse") // will be extended in the future
        when (error) {
            else -> {
                showFirstView(layout_error, layout_empty, layout_data)
            }
        }
        error.exception?.report()
    }

    private fun setupSkeleton() {
        fun Float.adjustValue(isSkeleton: Boolean) = (context?.adjustForDensity(this)
                ?: this).negateIfTrue(!isSkeleton)

        val skeletonDrawable = context?.getDrawable(R.drawable.all_skeleton_text)
                ?: ColorDrawable(Color.TRANSPARENT)
        // Move bestday_textview_time down a little bit so Best Day skeletons are evenly distributed
        val bestDayDateTransformation = { view: View, isSkeleton: Boolean ->
            when (view.id) {
                R.id.bestday_textview_time -> {
                    view.translationY += 3f.adjustValue(isSkeleton)
                }
                R.id.textview_stats_item -> {
                    view.translationX += 8f.adjustValue(isSkeleton)
                }
            }
        }
        skeleton = Skeleton(
                layout_data as ViewGroup,
                skeletonBackground = skeletonDrawable,
                skeletonHeight = context?.resources?.getDimension(R.dimen.height_all_skeleton_text)?.toInt()
                        ?: 16,
                transform = bestDayDateTransformation
        )
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

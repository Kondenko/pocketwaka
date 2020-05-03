package com.kondenko.pocketwaka.screens.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.kondenko.pocketwaka.FragmentDatePicker
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.analytics.Screen
import com.kondenko.pocketwaka.analytics.ScreenTracker
import com.kondenko.pocketwaka.screens.menu.FragmentMenu
import com.kondenko.pocketwaka.screens.stats.FragmentStatsContainer
import com.kondenko.pocketwaka.screens.summary.FragmentSummaryContainer
import com.kondenko.pocketwaka.screens.summary.SummaryRangeViewModel
import com.kondenko.pocketwaka.utils.extensions.forEachNonNull
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.transaction
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FragmentContent : Fragment() {

    private val screenTracker: ScreenTracker by inject()

    private val refreshEvents = PublishSubject.create<Unit>()

    private val vm: SummaryRangeViewModel by sharedViewModel()

    private var refreshEventsDisposable: Disposable? = null

    private lateinit var fragmentSummary: FragmentSummaryContainer
    private val tagSummary = "summary"

    private lateinit var fragmentStatsContainer: FragmentStatsContainer
    private val tagStats = "stats"

    private lateinit var fragmentMenu: FragmentMenu
    private val tagMenu = "menu"

    private var activeFragment: Fragment? = null

    private val scrollingViewBehaviour = AppBarLayout.ScrollingViewBehavior()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
          inflater
                .cloneInContext(ContextThemeWrapper(activity, R.style.AppTheme))
                .inflate(R.layout.fragment_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        (activity as? AppCompatActivity?)?.setSupportActionBar(toolbar_main)
        showData()
        val fragmentDatePicker = childFragmentManager
              .findFragmentByTag(getString(R.string.summary_tag_fragment_date_picker)) as? FragmentDatePicker
        vm.titleChanges().observe(viewLifecycleOwner) {
            fragmentDatePicker?.setTitle(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_activity_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> refreshEvents.onNext(Unit)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showData() {
        fragmentSummary = childFragmentManager.findFragmentByTag(tagSummary) as? FragmentSummaryContainer
              ?: FragmentSummaryContainer()
        fragmentStatsContainer = childFragmentManager.findFragmentByTag(tagStats) as? FragmentStatsContainer
              ?: FragmentStatsContainer()
        fragmentMenu = childFragmentManager.findFragmentByTag(tagMenu) as? FragmentMenu
              ?: FragmentMenu()
        childFragmentManager.transaction {
            forEachNonNull(fragmentSummary to tagSummary, fragmentStatsContainer to tagStats, fragmentMenu to tagMenu) { (fragment, tag) ->
                if (childFragmentManager.findFragmentByTag(tag) == null) {
                    add(R.id.main_container, fragment, tag)
                }
                hide(fragment)
            }
        }
        main_bottom_navigation.setOnNavigationItemSelectedListener {
            refreshEventsDisposable?.dispose()
            when (it.itemId) {
                R.id.bottomnav_item_summaries -> showSummaries()
                R.id.bottomnav_item_stats -> showStats()
                R.id.bottomnav_item_menu -> showMenu()
            }
            true
        }
        main_bottom_navigation.selectedItemId = R.id.bottomnav_item_summaries
    }

    private fun showSummaries() {
        setFragment(fragmentSummary) {
            screenTracker.log(activity, Screen.Summary)
            showAppBar(false)
            container_summary_date_picker.isVisible = true
            refreshEventsDisposable = fragmentSummary.subscribeToRefreshEvents(refreshEvents)
        }
    }

    private fun showStats() {
        setFragment(fragmentStatsContainer) {
            /*
                When this screen is opened for the first time in the session, selected tab is null.
                OnPageChangeListener gets called and sends a screen_view event with the default tab (e.g. 7 days).
                However when is screen is revisited during the same session, OnPageChangeListener is not called,
                but selected tab is initialized, so we report this event here.
            */
            fragmentStatsContainer.getSelectedTab()?.let { selectedTab ->
                screenTracker.log(activity, Screen.Stats(selectedTab))
            }
            showAppBar(true)
            container_summary_date_picker.isVisible = false
            refreshEventsDisposable = fragmentStatsContainer.subscribeToRefreshEvents(refreshEvents)
        }
    }

    private fun showMenu() {
        setFragment(fragmentMenu) {
            screenTracker.log(activity, Screen.Menu)
            showAppBar(false)
            container_summary_date_picker.isVisible = false
        }
    }

    private fun showAppBar(show: Boolean) {
        appbar_main.isInvisible = !show
        main_container.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = if (show) scrollingViewBehaviour else null
        }
        main_container.requestLayout()
    }

    private fun setFragment(fragment: Fragment, onCompleted: (() -> Unit)? = null) {
        if (activeFragment == null || activeFragment?.tag != fragment.tag) {
            childFragmentManager.transaction {
                activeFragment?.let { hide(it) }
                setCustomAnimations(R.anim.bottom_nav_in, R.anim.bottom_nav_out)
                show(fragment)
                runOnCommit {
                    activeFragment = fragment
                    onCompleted?.invoke()
                }
            }
        }
    }

}

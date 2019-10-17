package com.kondenko.pocketwaka.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.screens.menu.FragmentMenu
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import com.kondenko.pocketwaka.screens.summary.FragmentSummary
import com.kondenko.pocketwaka.utils.extensions.observe
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.startActivity
import com.kondenko.pocketwaka.utils.extensions.transaction
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel { parametersOf(R.id.bottomnav_item_summaries) }

    private val refreshEvents = PublishSubject.create<Any>()

    private var refreshEventsDisposable: Disposable? = null

    private val fragmentSummary = FragmentSummary()
    private val tagSummary = "summary"

    private val fragmentStats = FragmentStats()
    private val tagStats = "stats"

    private val fragmentMenu = FragmentMenu()
    private val tagMenu = "menu"

    private val scrollingViewBehaviour = AppBarLayout.ScrollingViewBehavior()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        vm.tabSelections().observe(this) { selectedTab ->
            when (selectedTab) {
                R.id.bottomnav_item_summaries -> showSummaries()
                R.id.bottomnav_item_stats -> showRanges()
                R.id.bottomnav_item_menu -> showMenu()
            }
        }
        vm.state().observe(this) {
            when (it) {
                is MainState.ShowData -> showData()
                is MainState.ShowLoginScreen -> showLoginScreen()
                is MainState.LogOut -> logout()
                is MainState.Error -> showError(it.cause)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_refresh -> refreshEvents.onNext(Any())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showData() {
        main_bottom_navigation.setOnNavigationItemSelectedListener {
            refreshEventsDisposable?.dispose()
            vm.tabChanged(it.itemId)
            true
        }
    }

    private fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logout() {
        finish()
        startActivity<LoginActivity>()
    }

    private fun showError(throwable: Throwable?) {
        throwable?.report()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    private fun showSummaries() {
        showAppBar(true)
        setFragment(fragmentSummary, tagSummary)
        refreshEventsDisposable = fragmentSummary.subscribeToRefreshEvents(refreshEvents)
    }

    private fun showRanges() {
        showAppBar(true)
        setFragment(fragmentStats, tagStats)
        refreshEventsDisposable = fragmentStats.subscribeToRefreshEvents(refreshEvents)
    }

    private fun showMenu() {
        setFragment(fragmentMenu, tagMenu)
        showAppBar(false)
    }

    private fun showAppBar(show: Boolean) {
        appbar_main.isVisible = show
        main_container.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = if (show) scrollingViewBehaviour else null
        }
        main_container.requestLayout()
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.transaction {
                replace(R.id.main_container, fragment, tag)
            }
        }
    }

}

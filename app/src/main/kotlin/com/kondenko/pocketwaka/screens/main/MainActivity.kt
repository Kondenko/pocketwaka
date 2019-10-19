package com.kondenko.pocketwaka.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.screens.menu.FragmentMenu
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import com.kondenko.pocketwaka.screens.summary.FragmentSummary
import com.kondenko.pocketwaka.utils.extensions.*
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel { parametersOf(R.id.bottomnav_item_summaries) }

    private val refreshEvents = PublishSubject.create<Unit>()

    private var refreshEventsDisposable: Disposable? = null

    private val fragmentSummary = FragmentSummary()
    private val tagSummary = "summary"

    private val fragmentStats = FragmentStats()
    private val tagStats = "stats"

    private val fragmentMenu = FragmentMenu()
    private val tagMenu = "menu"

    private var activeFragment: Fragment? = null

    private val scrollingViewBehaviour = AppBarLayout.ScrollingViewBehavior()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        supportFragmentManager.transaction {
            forEachNonNull(fragmentSummary to tagSummary, fragmentStats to tagStats, fragmentMenu to tagMenu) { (fragment, tag) ->
                add(R.id.main_container, fragment, tag)
                hide(fragment)
            }
        }
        vm.tabSelections().observe(this) { selectedTab ->
            when (selectedTab) {
                R.id.bottomnav_item_summaries -> showSummaries()
                R.id.bottomnav_item_stats -> showStats()
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
            R.id.action_refresh -> refreshEvents.onNext(Unit)
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

    private fun showSummaries() {
        setFragment(fragmentSummary) {
            showAppBar(true)
            refreshEventsDisposable = fragmentSummary.subscribeToRefreshEvents(refreshEvents)
        }
    }

    private fun showStats() {
        setFragment(fragmentStats) {
            showAppBar(true)
            refreshEventsDisposable = fragmentStats.subscribeToRefreshEvents(refreshEvents)
        }
    }

    private fun showMenu() {
        setFragment(fragmentMenu) {
            showAppBar(false)
        }
    }

    private fun showAppBar(show: Boolean) {
        appbar_main.isInvisible = !show
        main_container.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            behavior = if (show) scrollingViewBehaviour else null
        }
        main_container.requestLayout()
    }

    private fun showError(throwable: Throwable?) {
        throwable?.report()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    private fun setFragment(fragment: Fragment, onCompleted: (() -> Unit)? = null) {
        if (activeFragment == null || activeFragment?.tag != fragment.tag) {
            supportFragmentManager.transaction {
                activeFragment?.let { hide(it) }
                setCustomAnimations(R.anim.bottom_nav_in, R.anim.bottom_nav_out)
                show(fragment)
                activeFragment = fragment
                onCompleted?.let {
                    runOnCommit { it() }
                }
            }
        }
    }

}

package com.kondenko.pocketwaka.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    private val refreshEvents = PublishSubject.create<Any>()

    private var refreshEventsDisposable: Disposable? = null

    private val fragmentSummary = FragmentSummary()
    private val tagSummary = "summary"

    private val fragmentStats = FragmentStats()
    private val tagStats = "stats"

    private val fragmentMenu = FragmentMenu()
    private val tagMenu = "menu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)
        val visibility = window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = visibility
        vm.states().observe(this) {
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
            when (it.itemId) {
                R.id.bottomnav_item_summaries -> showSummaries()
                R.id.bottomnav_item_stats -> showRanges()
                R.id.bottomnav_item_menu -> showMenu()
            }
            true
        }
        main_bottom_navigation.selectedItemId = R.id.bottomnav_item_summaries
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
        appbar_main.visible()
        setFragment(fragmentSummary, tagSummary)
        refreshEventsDisposable = fragmentSummary.subscribeToRefreshEvents(refreshEvents)
    }

    private fun showRanges() {
        appbar_main.visible()
        setFragment(fragmentStats, tagStats)
        refreshEventsDisposable = fragmentStats.subscribeToRefreshEvents(refreshEvents)
    }

    private fun showMenu() {
        setFragment(fragmentMenu, tagMenu)
        appbar_main.gone()
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.transaction {
                replace(R.id.main_container, fragment, tag)
            }
        }
    }

}

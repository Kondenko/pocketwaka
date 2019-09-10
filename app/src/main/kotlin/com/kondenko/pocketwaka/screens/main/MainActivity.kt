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
import androidx.lifecycle.Observer
import com.kondenko.pocketwaka.FragmentMenu
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.daily.FragmentSummary
import com.kondenko.pocketwaka.screens.login.LoginActivity
import com.kondenko.pocketwaka.screens.ranges.FragmentRanges
import com.kondenko.pocketwaka.utils.extensions.report
import com.kondenko.pocketwaka.utils.extensions.transaction
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val vm: MainViewModel by viewModel()

    private val refreshEvents = PublishSubject.create<Any>()

    private val rangesFragment = FragmentRanges()
    private val tagRanges = "ranges"

    private val dailyFragment = FragmentSummary()
    private val tagDaily = "day"

    private val menuFragment = FragmentMenu()
    private val tagMenu = "menu"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.elevation = 0f
        val visibility = window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = visibility
        main_bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.bottomnav_item_today -> showDailyStats()
                R.id.bottomnav_item_ranges -> showRanges()
                R.id.bottomnav_item_menu -> showMenu()
            }
            true
        }
        main_bottom_navigation.selectedItemId = R.id.bottomnav_item_today
        vm.states().observe(this, Observer {
            when (it) {
                is MainState.ShowLoginScreen -> showLoginScreen()
                is MainState.LogOut -> logout()
                is MainState.Error -> showError(it.cause)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> vm.logout()
            R.id.action_refresh -> refreshEvents.onNext(Any())
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLoginScreen() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showError(throwable: Throwable?) {
        throwable?.report()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    private fun logout() {
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showDailyStats() {
        setFragment(dailyFragment, tagDaily)
    }

    private fun showRanges() {
        rangesFragment.subscribeToRefreshEvents(refreshEvents)
        setFragment(rangesFragment, tagRanges)
    }

    private fun showMenu() {
        setFragment(menuFragment, tagMenu)
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.transaction {
                replace(R.id.main_container, fragment, tag)
            }
        }
    }

}

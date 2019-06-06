package com.kondenko.pocketwaka.screens.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.auth.AuthActivity
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import com.kondenko.pocketwaka.utils.report
import com.kondenko.pocketwaka.utils.transaction
import io.reactivex.subjects.PublishSubject
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), MainView {

    private val tagStats = "stats"

    private val presenter: MainActivityPresenter by inject()

    private val refreshEvents = PublishSubject.create<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.screen_stats)
        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
    }

    override fun onStop() {
        presenter.detach()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> presenter.logout()
            R.id.action_refresh -> refreshEvents.onNext(Any())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoginScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showStats() {
        val statsFragment = FragmentStats()
        statsFragment.subscribeToRefreshEvents(refreshEvents)
        setFragment(statsFragment, tagStats)
    }

    override fun showError(throwable: Throwable?, messageStringRes: Int?) {
        throwable?.report()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    override fun onLogout() {
        finish()
        startActivity(Intent(this, AuthActivity::class.java))
    }

    private fun setFragment(fragment: androidx.fragment.app.Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.transaction {
                replace(R.id.container, fragment, tag)
            }
        }
    }

}

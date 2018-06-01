package com.kondenko.pocketwaka.screens.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.kondenko.pocketwaka.App
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.screens.auth.AuthActivity
import com.kondenko.pocketwaka.screens.stats.FragmentStats
import com.kondenko.pocketwaka.utils.transaction
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject


class MainActivity : AppCompatActivity(), MainView {

    private val tagStats = "stats"

    @Inject
    lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.mainComponent.inject(this)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)

    }

    override fun onStop() {
        super.onStop()
        presenter.detach()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> presenter.logout()
            R.id.action_refresh -> EventBus.getDefault().post(RefreshEvent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun showLoginScreen() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun showStats() {
        val stats = FragmentStats()
        setFragment(stats, tagStats)
    }

    override fun onError(throwable: Throwable?, messageStringRes: Int?) {
        throwable?.printStackTrace()
        Toast.makeText(this, R.string.error_refreshing_token, Toast.LENGTH_LONG).show()
    }

    override fun onLogout() {
        finish()
        startActivity(Intent(this, AuthActivity::class.java))
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag) == null) {
            supportFragmentManager.transaction {
                replace(R.id.container, fragment, tag)
            }
        }
    }

}

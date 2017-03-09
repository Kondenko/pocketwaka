package com.kondenko.pocketwaka.screens.activities.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.screens.activities.login.LoginActivity
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsContainer
import org.greenrobot.eventbus.EventBus


class MainActivity : AppCompatActivity(), MainActivityView {

    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stats = FragmentStatsContainer()
        setFragment(stats)
        presenter = MainActivityPresenter(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> logout()
            R.id.action_refresh -> EventBus.getDefault().post(RefreshEvent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            replace(R.id.container, fragment)
            commit()
        }
    }

    private fun logout() {
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
        AccessTokenUtils.deleteToken(this)
    }
}

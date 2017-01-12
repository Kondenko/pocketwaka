package com.kondenko.pocketwaka.screens.activities.main

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.screens.fragments.calendar.FragmentCalendar
import com.kondenko.pocketwaka.screens.fragments.leaders.FragmentLeaders
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStats
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsContainer


class MainActivity : AppCompatActivity(), MainActivityView {

    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stats = FragmentStatsContainer()
//        val calendar = FragmentCalendar()
//        val leaderboard = FragmentLeaders()

        setFragment(stats)

        /*
        val navigation = findViewById(R.id.bottomNavigationView) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener({ item ->
            when (item.itemId) {
                R.id.action_stats -> setFragment(stats)
                R.id.action_calendar -> setFragment(calendar)
                R.id.action_leaders -> setFragment(leaderboard)
            }
            true
        })
        var elevation: TypedValue = TypedValue()
        resources.getValue(R.dimen.elevation_bottom_nav, elevation, true)
        ViewCompat.setElevation(navigation, 8f)
        */

        presenter = MainActivityPresenter(this)
    }


    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            replace(R.id.container, fragment)
            commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> presenter.logout(this)
        }
        return super.onOptionsItemSelected(item)
    }

}

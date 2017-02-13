package com.kondenko.pocketwaka.screens.activities.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.events.ErrorEvent
import com.kondenko.pocketwaka.events.RefreshEvent
import com.kondenko.pocketwaka.events.SuccessEvent
import com.kondenko.pocketwaka.screens.fragments.states.FragmentErrorState
import com.kondenko.pocketwaka.screens.fragments.stats.FragmentStatsContainer
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), MainActivityView {

    private val TAG = this.javaClass.simpleName

    private lateinit var presenter: MainActivityPresenter

    private var selectedTag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val stats = FragmentStatsContainer()
        setInitialState(stats, "stats")
        presenter = MainActivityPresenter(this)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        EventBus.getDefault().unregister(this)
        super.onStop()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onError(event: ErrorEvent) {
        Log.i(TAG, "onError")
        val errorFragment = getErrorFragment()
        val selectedFragment = getSelectedFragment()
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            hide(selectedFragment)
            show(errorFragment)
            commit()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSuccess(event: SuccessEvent) {
        Log.i(TAG, "onSuccess")
        val errorFragment = getErrorFragment()
        val selectedFragment = getSelectedFragment()
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            show(selectedFragment)
            hide(errorFragment)
            commit()
        }
    }

    private fun setFragment(fragment: Fragment, tag: String) {
        selectedTag = tag
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            replace(R.id.container, fragment, tag)
            commit()
        }
    }

    private fun setInitialState(fragment: Fragment, tag: String) {
        selectedTag = tag
        val errorFragment = FragmentErrorState()
        val transaction = supportFragmentManager.beginTransaction()
        with(transaction) {
            replace(R.id.container, fragment, tag)
            add(R.id.container, errorFragment, FragmentErrorState.TAG)
            hide(errorFragment)
            commit()
        }
    }

    private fun getSelectedFragment() = selectedTag?.let { supportFragmentManager.findFragmentByTag(it) }

    private fun getErrorFragment(): FragmentErrorState {
        val errorFragment: FragmentErrorState = supportFragmentManager.findFragmentByTag(FragmentErrorState.TAG) as FragmentErrorState
        errorFragment.setOnUpdateListener {
            // TODO Display loading state
            postRefreshEvent()
        }
        return errorFragment
    }

    private fun postRefreshEvent() {
        Log.i(TAG, "postRefreshEvent")
        EventBus.getDefault().postSticky(RefreshEvent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_logout -> presenter.logout(this)
            R.id.action_refresh -> postRefreshEvent()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
            super.onBackPressed()
    }
}

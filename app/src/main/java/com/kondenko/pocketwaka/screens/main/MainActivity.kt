package com.kondenko.pocketwaka.screens.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.oauth.AccessTokenUtils


class MainActivity : AppCompatActivity(), MainActivityView {

    private lateinit var presenter: MainActivityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = MainActivityPresenter(this)
        try {
            Log.i("TOKEN", AccessTokenUtils.getFromPreferences(this).toString())
            Log.i("TOKEN", AccessTokenUtils.getToken(this))
        } catch (e: Exception) {
            e.printStackTrace()
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

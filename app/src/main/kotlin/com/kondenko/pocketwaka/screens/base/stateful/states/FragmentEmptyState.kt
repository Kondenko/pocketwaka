package com.kondenko.pocketwaka.screens.base.stateful.states


import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R
import kotlinx.android.synthetic.main.layout_stats_state_empty.*

class FragmentEmptyState : Fragment() {

    val TAG = "fragment_empty_state"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.layout_stats_state_empty, container, false)
        RxView.clicks(button_emptystate_plugins).subscribe {
            val uri = Const.URL_PLUGINS
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(ContextCompat.getColor(context!!, R.color.color_primary))
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(uri))
        }
        return view
    }

}

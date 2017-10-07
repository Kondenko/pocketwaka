package com.kondenko.pocketwaka.screens.states


import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.kondenko.pocketwaka.Const
import com.kondenko.pocketwaka.R

class FragmentEmptyState : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_stats_state_empty, container, false)
        val updateButton = view.findViewById<Button>(R.id.button_error_state_update)
        updateButton.setOnClickListener {
            val uri = Const.URL_PLUGINS
            val builder = CustomTabsIntent.Builder()
            builder.setToolbarColor(R.color.color_primary)
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(context, Uri.parse(uri))
        }
        return view
    }

}

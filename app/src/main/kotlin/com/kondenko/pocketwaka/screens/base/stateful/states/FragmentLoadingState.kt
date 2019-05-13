package com.kondenko.pocketwaka.screens.base.stateful.states


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.R

class FragmentLoadingState : androidx.fragment.app.Fragment() {

    val TAG = "FragmentLoadingState"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(R.layout.layout_all_state_loading, container, false)

}

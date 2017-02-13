package com.kondenko.pocketwaka.screens.fragments.states


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kondenko.pocketwaka.R


/**
 * A simple [Fragment] subclass.
 */
class FragmentErrorState : Fragment() {

    companion object {
        val TAG = "ErrorStateFragment"
    }

    private var updateAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_error_state, container, false)
        val updateButton = view.findViewById(R.id.button_error_state_update)
        updateButton.setOnClickListener { updateAction?.invoke() }
        return view
    }

    fun setOnUpdateListener(action: () -> Unit) {
        updateAction = action
    }

}

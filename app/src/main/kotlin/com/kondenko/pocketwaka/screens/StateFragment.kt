package com.kondenko.pocketwaka.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import kotlinx.android.synthetic.main.fragment_state.*
import kotlinx.android.synthetic.main.fragment_state.view.*
import timber.log.Timber

class StateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(context, R.layout.fragment_state, container)
    }

    @Throws(UnsupportedOperationException::class)
    fun <T> setState(state: State<T>, onActionClick: (() -> Unit)? = null) {
        var drawableRes: Int? = null
        var titleRes: Int? = null
        var subtitleRes: Int? = null
        when (state) {
            is State.Offline, is State.Failure.NoNetwork -> {
                drawableRes = R.drawable.img_offline
                titleRes = R.string.offline_state_title
                subtitleRes = R.string.offline_state_subtitle
                button_state_action_retry.isVisible = false
                button_state_action_open_plugins.isVisible = false
            }
            State.Empty -> {
                drawableRes = R.drawable.state_empty_img
                titleRes = R.string.empty_state_title
                subtitleRes = R.string.empty_state_subtitle
                button_state_action_open_plugins.isVisible = true
                button_state_action_retry.isVisible = false
                onActionClick?.let {
                    button_state_action_open_plugins.setOnClickListener { it() }
                }
            }
            is State.Failure.UnknownRange, is State.Failure.Unknown -> {
                drawableRes = R.drawable.state_error_img
                titleRes = R.string.error_state_title
                subtitleRes = R.string.error_state_subtitle
                button_state_action_retry.isVisible = true
                button_state_action_open_plugins.isVisible = false
                onActionClick?.let {
                    button_state_action_retry.setOnClickListener { it() }
                }
            }
            else -> {
                Timber.w("This state is not supported: $state")
            }
        }
        view?.run {
            drawableRes?.let { imageview_state_illustration?.setImageDrawable(context.getDrawable(it)) }
            titleRes?.let { textview_state_title.setText(it) }
            subtitleRes?.let { textview_state_subtitle.setText(it) }
        }
    }

}
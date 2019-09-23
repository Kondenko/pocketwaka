package com.kondenko.pocketwaka.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.transaction
import kotlinx.android.synthetic.main.fragment_state.*
import kotlinx.android.synthetic.main.fragment_state.view.*
import timber.log.Timber

fun Fragment.lazyStateFragment(@IdRes containerId: Int, fragmentSupplier: (() -> StateFragment)? = null) = lazy {
    val fragment = fragmentSupplier?.invoke() ?: StateFragment()
    childFragmentManager.transaction {
        add(containerId, fragment, null)
    }
    fragment
}

open class StateFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    @Throws(UnsupportedOperationException::class)
    open fun setState(state: State<*>, onActionClick: (() -> Unit)? = null) {
        var drawableRes: Int? = null
        var titleRes: Int? = null
        var subtitleRes: Int? = null
        when (state) {
            is State.Offline<*>, is State.Failure.NoNetwork<*> -> {
                drawableRes = R.drawable.img_offline
                titleRes = R.string.offline_state_title
                subtitleRes = R.string.offline_state_subtitle
                button_state_action_retry.isVisible = false
                button_state_action_open_plugins.isVisible = false
            }
            State.Empty -> {
                drawableRes = R.drawable.img_state_empty
                titleRes = R.string.empty_state_title
                subtitleRes = R.string.empty_state_subtitle
                button_state_action_open_plugins.isVisible = true
                button_state_action_retry.isVisible = false
                onActionClick?.let {
                    button_state_action_open_plugins.setOnClickListener { it() }
                }
            }
            is State.Failure.InvalidParams<*>, is State.Failure.Unknown<*> -> {
                drawableRes = R.drawable.img_state_error
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
        setContent(drawableRes, titleRes, subtitleRes)
    }

    protected fun setContent(drawableRes: Int? = null, titleRes: Int? = null, subtitleRes: Int? = null) {
        view?.run {
            drawableRes?.let { imageview_state_illustration?.setImageDrawable(context.getDrawable(it)) }
            titleRes?.let { textview_state_title.setText(it) }
            subtitleRes?.let { textview_state_subtitle.setText(it) }
        }
    }

}
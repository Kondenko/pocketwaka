package com.kondenko.pocketwaka.screens

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.types.Either
import com.kondenko.pocketwaka.utils.types.left
import com.kondenko.pocketwaka.utils.types.right
import kotlinx.android.synthetic.main.fragment_state.*
import timber.log.Timber
import kotlin.math.roundToInt

open class StateFragment : Fragment() {

    private lateinit var offlineIllustrationDrawable: Drawable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val illustrationMargin = context.resources.getDimension(R.dimen.margin_state_screen_image_offline).roundToInt()
        offlineIllustrationDrawable = InsetDrawable(
                context.getDrawable(R.drawable.img_offline),
                illustrationMargin,
                0,
                illustrationMargin,
                illustrationMargin / 2
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_state, container, false)
    }

    open fun setState(state: State<*>, onActionClick: (() -> Unit)? = null) {
        var drawableRes: Either<Drawable, Int>? = null
        var titleRes: Int? = null
        var subtitleRes: Int? = null
        when (state) {
            is State.Offline<*>, is State.Failure.NoNetwork<*> -> {
                drawableRes = offlineIllustrationDrawable.left()
                titleRes = R.string.offline_state_title
                subtitleRes = R.string.offline_state_subtitle
                button_state_action_retry.isVisible = false
                button_state_action_open_plugins.isVisible = false
            }
            is State.Empty -> {
                drawableRes = R.drawable.img_state_empty.right()
                titleRes = R.string.empty_state_title
                subtitleRes = R.string.empty_state_subtitle
                button_state_action_open_plugins.isVisible = true
                button_state_action_retry.isVisible = false
                onActionClick?.let {
                    button_state_action_open_plugins.setOnClickListener { it() }
                }
            }
            is State.Failure.InvalidParams<*>, is State.Failure.Unknown<*> -> {
                drawableRes = R.drawable.img_state_error.right()
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

    protected fun setContent(drawable: Either<Drawable, Int>? = null, title: Int? = null, subtitle: Int? = null) {

        fun TextView.setText(stringRes: Int?) = stringRes?.let(::setText) ?: setText(null)

        textview_state_title?.setText(title)
        textview_state_subtitle?.setText(subtitle)
        imageview_state_illustration?.apply {
            drawable?.left?.let(::setImageDrawable) ?: drawable?.right?.let(::setImageResource)
        }

    }

}
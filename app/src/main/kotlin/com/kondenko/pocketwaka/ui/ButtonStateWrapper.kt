package com.kondenko.pocketwaka.ui

import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import timber.log.Timber

class ButtonStateWrapper
@JvmOverloads constructor(
        private val button: Button,
        private val loadingView: View? = null,
        private val retryText: CharSequence? = null,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : FrameLayout(button.context, attrs, defStyleAttr, defStyleRes) {

    companion object {

        fun wrap(button: Button, loadingView: View? = null, retryText: CharSequence? = null): ButtonStateWrapper {
            return ButtonStateWrapper(button, loadingView, retryText)
        }

    }

    private val text = button.text

    var isDefault: Boolean = true
        private set

    var isLoading: Boolean = false
        private set

    var isError: Boolean = false
        private set

    init {
        layoutParams = LayoutParams(button.layoutParams)
        button.post {
            x = button.x
            y = button.y
            (button.parent as ViewGroup).let {
                it.removeView(button)
                it.addView(this)
                addView(button)
            }
        }
    }

    fun setDefault() {
        Timber.d("Default state set")
        button.text = text
        loadingView?.isGone = true
        button.isClickable = false
        isDefault = true
        isLoading = false
        isError = false
    }

    fun setLoading() {
        Timber.d("Loading state set")
        button.text = null
        loadingView?.isVisible = true
        button.isClickable = false
        isDefault = false
        isLoading = true
        isError = false
    }

    fun setError() {
        Timber.d("Error state set")
        button.text = retryText
        loadingView?.isGone = true
        button.isClickable = false
        isDefault = false
        isLoading = false
        isError = true
    }

}
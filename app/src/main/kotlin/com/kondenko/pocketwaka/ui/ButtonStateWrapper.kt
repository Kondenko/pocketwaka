package com.kondenko.pocketwaka.ui

import android.view.View
import android.widget.Button
import androidx.core.view.isGone
import androidx.core.view.isVisible

class ButtonStateWrapper(
        private val button: Button,
        private val loadingView: View,
        private val retryText: CharSequence
) {

    private val text = button.text

    var isDefault: Boolean = true
        private set

    var isLoading: Boolean = false
        private set

    var isError: Boolean = false
        private set

    fun setDefault() {
        button.text = text
        loadingView.isGone = true
        button.isClickable = true
        isDefault = true
        isLoading = false
        isError = false
    }

    fun setLoading() {
        button.text = null
        loadingView.isVisible = true
        button.isClickable = false
        isDefault = false
        isLoading = true
        isError = false
    }

    fun setError() {
        button.text = retryText
        loadingView.isGone = true
        button.isClickable = true
        isDefault = false
        isLoading = false
        isError = true
    }

}
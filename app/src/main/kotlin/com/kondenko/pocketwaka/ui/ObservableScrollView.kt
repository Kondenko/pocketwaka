package com.kondenko.pocketwaka.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

/**
 * ScrollView with scrolling callbacks.
 */
class ObservableScrollView(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : ScrollView(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0)

    private var listener: OnScrollViewListener? = null

    fun setOnScrollListener(listener: OnScrollViewListener) {
        this.listener = listener
    }

    override fun onScrollChanged(x: Int, y: Int, oldX: Int, oldY: Int) {
        listener?.onScrollChanged(this, x, y, oldX, oldY)
        super.onScrollChanged(x, y, oldX, oldY)
    }

}
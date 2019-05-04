package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import com.kondenko.pocketwaka.R

class LoadingView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var dotsNumber: Int

    private var dotDrawable: Drawable

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, defStyleRes)) {
            dotsNumber = getInteger(R.styleable.LoadingView_dots_number, 3)
            dotDrawable = getDrawable(R.styleable.LoadingView_dot_drawable)
            recycle()
        }
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        construct()
    }

    fun setDotsNumber(number: Int) {
        dotsNumber = number
        construct()
    }

    fun setDotDrawable(drawable: Drawable) {
        dotDrawable = drawable
        construct()
    }

    private fun construct() {
        children.forEach { removeView(it) }
        weightSum = dotsNumber.toFloat()
        for (i in 1..dotsNumber) {
            addView(ImageView(context).apply {
                setImageDrawable(dotDrawable)
                layoutParams = LayoutParams(dotDrawable.intrinsicWidth, dotDrawable.intrinsicHeight, 1f)
            })
        }
    }

}

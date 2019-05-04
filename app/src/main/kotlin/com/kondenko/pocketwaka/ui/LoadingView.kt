package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout

class LoadingView @JvmOverloads constructor(
        context: Context,
        private val dot: Drawable,
        dotsNumber: Int,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    init {
        orientation = HORIZONTAL
        for (i in 1..dotsNumber) {
            addView(ImageView(context).apply {
                setImageDrawable(dot)
            })
        }
    }

}

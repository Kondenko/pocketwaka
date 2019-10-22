package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.IntRange

class RoundedProgressBar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeCap = Paint.Cap.ROUND
    }

    var progress: Int = 0
        set(@IntRange(from = 0, to = 100) value) {
            field = value
            invalidate()
        }

    var color: Int = Color.BLACK
        set(@ColorInt value) {
            field = value
            paint.color = value
            invalidate()
        }

    private val progressBarWidth: Float
        get() = progress / 100f * width

    init {
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        paint.strokeWidth = measuredHeight.toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawLine(height / 2f /*cap radius*/, height / 2f, progressBarWidth, height / 2f, paint)
    }

}
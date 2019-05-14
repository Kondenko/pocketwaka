package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.kondenko.pocketwaka.utils.applyMatrix
import com.kondenko.pocketwaka.utils.createPath

class SquircleProgressBar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var progress = 0.5f
        set(@FloatRange(from = 0.0, to = 1.0) value) {
            field = value
            invalidate()
        }

    var text: String? = null

    var color: Int = Color.BLACK
        set(@ColorInt value) {
            field = value
            paint.color = value
            invalidate()
        }

    var progressBarHeight: Int = 0
        set(value) {
            field = value
            radius = getRadius(value)
            invalidate()
        }

    val progressBarWidth: Float
        get() {
            val actualWidth = progress * width
            return if (actualWidth > radius * 2f) actualWidth else progressBarHeight.toFloat()
        }

    private var radius = getRadius(progressBarHeight)

    init {
        setWillNotDraw(false)
        paint.color = color
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        progressBarHeight = measuredHeight
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val path = getSquirclePath(0f, 0f)
        canvas?.drawPath(path, paint)
    }

    private fun getSquirclePath(left: Float, top: Float): Path {
        val radiusToPow = (radius * radius * radius).toDouble()
        val path = createPath {
            moveTo(-radius.toFloat(), 0f)
            (-radius..radius).map(Int::toFloat).forEach { y ->
                lineTo(getX(y), Math.cbrt(radiusToPow - Math.abs(y * y * y)).toFloat())
            }
            (radius downTo -radius).map(Int::toFloat).forEach { y ->
                lineTo(getX(y), (-Math.cbrt(radiusToPow - Math.abs(y * y * y))).toFloat())
            }
        }
        path.applyMatrix {
            postTranslate(left + radius, top + radius)
        }
        return path
    }

    private fun getX(y: Float) = if (y < 0) y else (y + progressBarWidth - radius * 2)

    private fun getRadius(height: Int) = height / 2

}
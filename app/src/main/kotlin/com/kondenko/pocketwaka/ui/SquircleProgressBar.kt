package com.kondenko.pocketwaka.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import com.kondenko.pocketwaka.utils.applyMatrix
import com.kondenko.pocketwaka.utils.createPath
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity

class SquircleProgressBar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    @FloatRange(from = 0.0, to = 1.0)
    var progress = 0f

    var text: String? = null

    @ColorInt
    var color: Int = Color.BLACK

    val height = context.adjustForDensity(32)

    val radius = height.toInt() / 2

    init {
        setWillNotDraw(false)
        paint.color = color
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

    private fun getX(y: Float) = if (y < 0) y else (y + measuredWidth - radius * 2)

}
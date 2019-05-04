package com.kondenko.pocketwaka.ui.onelinesegmentedchart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import com.kondenko.pocketwaka.R
import java.util.*

/**
 * Horizontal bar with segments, which represent a set of data.
 *
 * Warning: this view is pretty unstable and requires testing and improvements.
 *
 * @see Segment
 * @see <a href="http://stackoverflow.com/a/26201117">Stack overflow question</a> where the code for masking was taken from
 */
class OneLineSegmentedChart(context: Context, private val attrs: AttributeSet? = null) : View(context, attrs) {

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val maskPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private var maskBitmap: Bitmap? = null

    private var segments = ArrayList<Segment>()
    private var coloredRectangles = ArrayList<Pair<RectF, Int>>()

    private var barWidth: Float
    private var cornerRadius: Float
    private var sortDescending: Boolean
    private var barTop: Float = 0f

    init {
        // Defaults
        barWidth = adjustForDensity(18f)
        cornerRadius = 0f
        sortDescending = true

        // Get the attributes defined in XML (if there are any)
        attrs?.let {
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.OneLineSegmentedChart, 0, 0)
            barWidth = attributes.getDimension(R.styleable.OneLineSegmentedChart_bar_width, barWidth)
            cornerRadius = attributes.getDimension(R.styleable.OneLineSegmentedChart_corner_radius, cornerRadius)
            sortDescending = attributes.getBoolean(R.styleable.OneLineSegmentedChart_sortDescending, sortDescending)
            attributes.recycle()
        }

        cornerRadius = adjustForDensity(cornerRadius)
        barWidth = adjustForDensity(barWidth)

        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        // Setting the Y point of the bar so it's centered horizontally in the view
        barTop = measuredHeight / 2 - barWidth / 2
        generateColoredRectangles()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        // Draw the segments
        for (coloredRectangle in coloredRectangles) {
            paint.color = coloredRectangle.second
            canvas?.drawRect(coloredRectangle.first, paint)
        }
        invalidate()
    }

    override fun draw(canvas: Canvas) {
        val offscreenBitmap = Bitmap.createBitmap(canvas.width, canvas.height, Bitmap.Config.ARGB_8888)
        val offscreenCanvas = Canvas(offscreenBitmap)
        super.draw(offscreenCanvas)
        if (maskBitmap == null) maskBitmap = createMask(canvas.width, canvas.height)
        offscreenCanvas.drawBitmap(maskBitmap, 0f, 0f, maskPaint)
        canvas.drawBitmap(offscreenBitmap, 0f, 0f, paint)
    }

    /**
     * Crates a mask so that the view appears with rounded corners
     * of predetermined value.
     */
    private fun createMask(w: Int, h: Int): Bitmap {
        val mask = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        val canvas = Canvas(mask)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        canvas.drawRoundRect(RectF(0f, 0f, w.toFloat(), h.toFloat()), cornerRadius, cornerRadius, paint)
        return mask
    }

    /**
     * Generate an array of pairs of [RectF] objects and their colors
     * based on [Segment]s.
     *
     * These rectangles will be used to draw segments of data.
     */
    private fun generateColoredRectangles() {
        if (coloredRectangles.isNotEmpty()) coloredRectangles.clear()
        segments.run {
            if (sortDescending) sortedByDescending { it.percent }
            else sortedBy { it.percent }
        }
        var left = 0f
        for ((weight, color) in segments) {
            val width = weight * measuredWidth / 100
            val right = left + width
            val rect = RectF(left, barTop, right, barWidth)
            coloredRectangles.add(Pair(rect, color))
            left += width
        }
    }

    /**
     * Check if the value is correct and return it adjusted
     *
     * @param value number to check
     * @param valueName what value to log if the check is not passed
     */
    private fun safeSet(value: Float, valueName: String): Float {
        if (value > 0) return adjustForDensity(value)
        else throw IllegalArgumentException("$valueName can't be less than 0")
    }

    /**
     * Update a view's dimension so it matches the device's density
     */
    private fun adjustForDensity(v: Float) = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, v, context.resources.displayMetrics))

    fun addSegment(segment: Segment) {
        segments.add(segment)
        update()
    }

    fun addSegments(segments: List<Segment>) {
        this.segments.addAll(segments)
        update()
    }

    fun setSegments(segments: ArrayList<Segment>) {
        this.segments = segments
        update()
    }

    fun setBarWidth(w: Float) {
        barWidth = safeSet(w, "Padding")
    }

    fun setCornerRadius(radius: Float) {
        cornerRadius = safeSet(radius, "Corner radius")
    }

    fun setSortDescending(descending: Boolean) {
        sortDescending = descending
    }

    /**
     * Update the view after a dataset change
     */
    private fun update() {
        generateColoredRectangles()
        invalidate()
    }

}

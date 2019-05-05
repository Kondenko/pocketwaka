package com.kondenko.pocketwaka.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.IntRange
import androidx.core.animation.doOnEnd
import androidx.core.view.children
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.adjustForDensity

class LoadingView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var dotsNumber: Int

    private var dotDrawable: Drawable

    // From the original position to the upmost position
    private val travelDistanceUpper = context.adjustForDensity(4f)

    // From the original position to the downmost position
    private val travelDistanceLower = context.adjustForDensity(2f)

    // From the original position to the upmost position
    private val travelDurationOrigToUp = 120L

    // From the upmost position to the downmost position
    private val travelDurationUpToDown = 150L

    // From the downmost position to the original position
    private val travelDurationDownToOrig = 40L

    private val animDuration = travelDurationOrigToUp + travelDurationUpToDown + travelDurationDownToOrig

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, defStyleRes)) {
            dotsNumber = getInteger(R.styleable.LoadingView_dots_number, 3)
            dotDrawable = getDrawable(R.styleable.LoadingView_dot_drawable)
                    ?: context.getDrawable(R.drawable.loading_dot)!!
            recycle()
        }
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        construct()
        startAnimation()
    }

    private fun startAnimation() {
        children.forEachIndexed { index, dot ->
            dot.doAnimation(index)
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun View.doAnimation(@IntRange(from = 0) dotIndex: Int) {
        fun yAnimator(distance: Float) = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, distance)

        val set = AnimatorSet()
        val origToUp = yAnimator(-travelDistanceUpper).apply {
            duration = travelDurationOrigToUp
            interpolator = AccelerateInterpolator()
        }
        val upToDown = yAnimator(travelDistanceUpper + travelDistanceLower).apply {
            duration = travelDurationOrigToUp
            interpolator = DecelerateInterpolator()
        }
        val downToOrig = yAnimator(-travelDistanceLower).apply {
            duration = travelDurationOrigToUp
            interpolator = AccelerateInterpolator()
        }
        val delay = dotIndex * animDuration
        with(set) {
            play(origToUp).before(upToDown).before(downToOrig)
            playSequentially(origToUp, upToDown, downToOrig)
            setDuration(animDuration)
            setStartDelay(delay)
            doOnEnd {
                setStartDelay((dotsNumber - 1).coerceAtLeast(1) * animDuration)
                start()
            }
            start()
        }
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

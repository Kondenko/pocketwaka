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
import com.kondenko.pocketwaka.utils.IllegalViewUsageException
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import kotlin.math.roundToInt

class LoadingView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    var dotsNumber: Int = 3
        set(value) {
            field = value
            construct()
        }

    var dotDrawable: Drawable? = context.getDrawable(R.drawable.loading_dot)
        set(value) {
            field = value
            construct()
        }

    var dotMargin: Int = 2
        set(value) {
            field = context.adjustForDensity(value).roundToInt()
            construct()
        }

    // From the original position to the upmost position
    private val travelDistanceUpper = 6f

    // From the original position to the downmost position
    private val travelDistanceLower = 1f

    private val overshootDownToOrig = 0.5f

    // From the original position to the upmost position
    private val travelDurationOrigToUp = 120L

    // From the upmost position to the downmost position
    private val travelDurationUpToDown = 150L

    // From the downmost position to the original position
    private val travelDurationDownToOrig = 40L

    private val travelDurationDownOvershoot = 10L

    private val animDuration = calculateAnimDuration()

    private val animationsOffsetMs = 10

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, defStyleRes)) {
            dotsNumber = getInteger(R.styleable.LoadingView_dots_number, 3)
            dotMargin = getInteger(R.styleable.LoadingView_dot_margin, dotMargin)
            getDrawable(R.styleable.LoadingView_dot_drawable)?.let { dotDrawable = it }
            recycle()
        }
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        construct()
    }

    private fun construct() {
        val finalDotDrawable = dotDrawable ?: throw IllegalViewUsageException("Dot drawable must be set")
        removeAllViews()
        weightSum = dotsNumber.toFloat()
        for (i in 1..dotsNumber) {
            addView(ImageView(context).apply {
                setImageDrawable(finalDotDrawable)
                layoutParams = LayoutParams(finalDotDrawable.intrinsicWidth, finalDotDrawable.intrinsicHeight, 1f).apply {
                    setMargins(dotMargin, 0, dotMargin, 0)
                }
            })
        }
        startAnimation()
    }

    fun startAnimation() {
        children.forEachIndexed { index, dot ->
            dot.doAnimation(index)
        }
    }

    @Suppress("UsePropertyAccessSyntax")
    private fun View.doAnimation(@IntRange(from = 0) dotIndex: Int) {
        fun yAnimator(distance: Float, setup: ObjectAnimator.() -> Unit = {}): ObjectAnimator {
            return ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, distance).apply(setup)
        }

        val origToUp = yAnimator(-travelDistanceUpper) {
            duration = travelDurationOrigToUp
            interpolator = DecelerateInterpolator()
        }
        val upToDown = yAnimator(travelDistanceUpper + travelDistanceLower) {
            duration = travelDurationOrigToUp
            interpolator = AccelerateInterpolator()
        }
        val downToOrig = yAnimator(-travelDistanceLower + overshootDownToOrig) {
            duration = travelDurationOrigToUp - travelDurationDownOvershoot
            interpolator = DecelerateInterpolator()
        }
        val overshootDown = yAnimator(overshootDownToOrig) {
            duration = travelDurationDownToOrig / 4
            interpolator = AccelerateInterpolator()
        }

        val delay = dotIndex * animDuration + animationsOffsetMs
        val repeatDelay = (dotsNumber - 1).coerceAtLeast(1) * animDuration - animDuration

        with(AnimatorSet()) {
            playSequentially(origToUp, upToDown, downToOrig, overshootDown)
            setDuration(animDuration)
            setStartDelay(delay)
            doOnEnd {
                setStartDelay(repeatDelay)
                start()
            }
            start()
        }
    }

    private fun calculateAnimDuration() = travelDurationOrigToUp + travelDurationUpToDown + travelDurationDownToOrig

}

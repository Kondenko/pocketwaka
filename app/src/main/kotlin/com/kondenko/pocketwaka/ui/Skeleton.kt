package com.kondenko.pocketwaka.ui

import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.findViewsWithTag
import kotlin.math.roundToInt

private data class InitialState(
        val text: CharSequence?,
        val backgroundDrawable: Drawable?
)

class Skeleton(
        private val root: ViewGroup? = null,
        private val skeletonBackground: Drawable,
        private val skeletonHeight: Int? = null,
        private val transform: ((View, Boolean) -> Unit)? = null
) {

    var animDuration: Long = 300

    private val pulseAnimations: MutableSet<ValueAnimator> = mutableSetOf()

    private val initialStates: MutableMap<View, InitialState> by lazy { findViews(root) }

    fun addViews(root: ViewGroup) {
        findViews(root).forEach { (view, state) ->
            if (!initialStates.containsKey(view)) {
                initialStates[view] = state
            }
        }
    }

    fun addView(view: View) {
        if (view.getTag(R.id.tag_skeleton_width_key) != null) {
            initialStates[view] = view.getInitialState()
        }
    }

    private fun findViews(root: ViewGroup?) =
            root?.findViewsWithTag(R.id.tag_skeleton_width_key, null)
                    ?.associateWith { it.getInitialState() }
                    ?.toMutableMap()
                    ?: mutableMapOf()

    private fun View.getInitialState() = InitialState((this as? TextView)?.text, this.background)

    fun show() {
        initialStates.keys
                .forEach {
                    it.showSkeleton()
                    it.playPulseAnimation()
                }
    }

    fun hide() {
        initialStates.keys.forEach { it.hideSkeleton() }
        stopPulseAnimations()
    }

    private fun View.showSkeleton() {
        val dimenWidth = (getTag(R.id.tag_skeleton_width_key) as String?)?.toInt()
        val finalWidth = context.adjustForDensity(dimenWidth)?.roundToInt().let {
            if (it == null || it < 0) width else it
        }
        animateIn {
            this.updateLayoutParams {
                width = finalWidth
                height = skeletonHeight ?: height
            }
            transform?.invoke(this, true)
            background = skeletonBackground
            (this as? TextView)?.text = null
        }
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        animateIn {
            updateLayoutParams {
                this.width = ViewGroup.LayoutParams.WRAP_CONTENT
                this.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            transform?.invoke(this, false)
            background = it.backgroundDrawable
            (this as? TextView)?.text = it.text
        }
    }

    private fun View.animateIn(updateView: () -> Unit) {
        alpha = 0f
        animate()
                .withStartAction(updateView)
                .alpha(1f)
                .setDuration(if (!isShown) animDuration / 2 else animDuration)
                .start()
    }

    private fun View.playPulseAnimation() {
        val alphaDimmed = 0.59f
        val alphaFull = 1f
        pulseAnimations += ValueAnimator.ofFloat(alphaDimmed, alphaFull).apply {
            duration = 650L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { this@playPulseAnimation.alpha = it.animatedValue as Float }
            start()
        }
    }

    private fun stopPulseAnimations() = pulseAnimations.forEach(ValueAnimator::cancel)

}

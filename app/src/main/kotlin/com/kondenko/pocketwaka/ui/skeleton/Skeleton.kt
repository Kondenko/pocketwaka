package com.kondenko.pocketwaka.ui.skeleton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
        val backgroundDrawable: Drawable?,
        val width: Int,
        val height: Int
)

class Skeleton(
        private val context: Context,
        private val root: View? = null,
        var skeletonBackground: Drawable =
                context.getDrawable(R.drawable.all_skeleton_text) ?: ColorDrawable(Color.TRANSPARENT),
        var skeletonHeight: Int =
                context.resources.getDimension(R.dimen.height_all_skeleton_text).toInt(),
        var transform: ((View, Boolean) -> Unit)? = null
) {

    var animDuration: Long = 300

    private val pulseAnimations: MutableSet<ValueAnimator> = mutableSetOf()

    private val initialStates: MutableMap<View, InitialState> by lazy { findViews(root) }

    var isShown: Boolean = false
        private set

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

    private fun findViews(root: View?) =
            root?.findViewsWithTag(R.id.tag_skeleton_width_key, null)
                    ?.associateWith { it.getInitialState() }
                    ?.toMutableMap()
                    ?: mutableMapOf()

    private fun View.getInitialState() = InitialState((this as? TextView)?.text, this.background, width, height)

    fun show() {
        initialStates.keys
                .forEach {
                    it.showSkeleton()
                    it.playPulseAnimation()
                }
        isShown = true
    }

    fun hide() {
        initialStates.keys.forEach { it.hideSkeleton() }
        stopPulseAnimations()
        isShown = false
    }

    private fun View.showSkeleton() {
        val dimenWidth = (getTag(R.id.tag_skeleton_width_key) as String?)?.toInt()
        val finalWidth = this@Skeleton.context.adjustForDensity(dimenWidth)?.roundToInt().let {
            if (it == null || it < 0) width else it
        }
        animateIn {
            this.updateLayoutParams {
                width = finalWidth
                height = skeletonHeight
            }
            if (isShown) transform?.invoke(this, true)
            background = skeletonBackground
            (this as? TextView)?.text = null
        }
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        animateIn {
            updateLayoutParams {
                this.width = it.width
                this.height = it.height
            }
            if (!isShown) transform?.invoke(this, false)
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Skeleton
        if (root != other.root) return false
        return true
    }

    override fun hashCode(): Int {
        return root?.hashCode() ?: 0
    }


}

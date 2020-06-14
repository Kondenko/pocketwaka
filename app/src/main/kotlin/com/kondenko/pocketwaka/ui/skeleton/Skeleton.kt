package com.kondenko.pocketwaka.ui.skeleton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.dp
import com.kondenko.pocketwaka.utils.extensions.findViewsWithTag
import com.kondenko.pocketwaka.utils.extensions.setSize
import kotlin.math.roundToInt

class Skeleton(
      private val context: Context,
      private val root: View? = null,
      private val skeletonBackground: Drawable = context.getDrawable(R.drawable.all_skeleton)
            ?: ColorDrawable(Color.TRANSPARENT),
      private var onSkeletonShown: ((Boolean) -> Unit)? = null
) {

    private data class InitialState(
          val width: Int,
          val height: Int,
          val backgroundDrawable: Drawable?,
          val text: CharSequence?,
          val imageSource: Drawable?
    )

    private val pulseAnimations: MutableSet<ValueAnimator> = mutableSetOf()

    private val initialStates: MutableMap<View, InitialState> by lazy { findViews(root) }

    var isShown: Boolean = false
        private set

    fun onSkeletonShown(callback: ((Boolean) -> Unit)) {
        onSkeletonShown = callback
    }

    fun show() {
        onSkeletonShown?.invoke(true)
        initialStates.keys.forEach {
            it.showSkeleton()
            it.playPulseAnimation()
        }
        isShown = true
    }

    fun hide() {
        initialStates.keys.forEach { it.hideSkeleton() }
        stopPulseAnimations()
        onSkeletonShown?.invoke(false)
        isShown = false
    }

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

    fun cleanup() {
        onSkeletonShown = null
        initialStates.clear()
    }

    private fun findViews(root: View?) =
          root?.findViewsWithTag(R.id.tag_skeleton_width_key, null)
                ?.associateWith { it.getInitialState() }
                ?.toMutableMap()
                ?: mutableMapOf()

    private fun View.getInitialState() =
          InitialState(
                width,
                height,
                backgroundDrawable = this.background,
                text = (this as? TextView)?.text,
                imageSource = (this as? ImageView)?.drawable
          )

    private fun View.showSkeleton() {
        val fallbackWidth = width
        val skeletonWidth = getDimenFromTag(R.id.tag_skeleton_width_key) ?: fallbackWidth
        val fallbackHeight = resources.getDimension(R.dimen.height_all_skeleton_text).roundToInt()
        val skeletonHeight = getDimenFromTag(R.id.tag_skeleton_height_key) ?: fallbackHeight
        setSize(skeletonWidth, skeletonHeight)
        background = skeletonBackground
        (this as? TextView)?.text = null
        (this as? ImageView)?.setImageBitmap(null)
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        setSize(it.width, it.height)
        background = it.backgroundDrawable
        (this as? TextView)?.text = it.text
        (this as? ImageView)?.setImageDrawable(it.imageSource)
    }

    private fun View.getDimenFromTag(tagId: Int): Int? =
          (getTag(tagId) as? String?)
                ?.toIntOrNull()
                ?.takeIf { it >= 0 }
                ?.let(context::dp)
                ?.roundToInt()

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
